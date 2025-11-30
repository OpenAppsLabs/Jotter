package com.openapps.jotter.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Data class to hold the device's authentication support status.
 * Note: It's not possible to distinguish between PIN and Pattern with public APIs,
 * so they are tied to the presence of any secure device credential.
 */
data class AuthSupport(
    val hasFingerprint: Boolean,
    val hasDeviceCredential: Boolean // Covers PIN, Pattern, or Password
)

object BiometricAuthUtil {

    /**
     * Checks the device for available and enabled authentication methods.
     */
    fun getAuthenticationSupport(context: Context): AuthSupport {
        val biometricManager = BiometricManager.from(context)
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val hasBiometrics = biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
        val hasDeviceCredential = keyguardManager.isDeviceSecure

        return AuthSupport(
            hasFingerprint = hasBiometrics,
            hasDeviceCredential = hasDeviceCredential
        )
    }

    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        // Allow Biometric OR Device Credential (PIN/Pattern)
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Don't propagate cancellation errors
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        onError(errString.toString())
                    }
                }
            })

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            // Note: setNegativeButtonText is NOT allowed when DEVICE_CREDENTIAL is used

        biometricPrompt.authenticate(promptInfoBuilder.build())
    }
}