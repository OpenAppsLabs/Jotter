package com.openapps.jotter.ui.theme

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView

val LocalHapticEnabled = staticCompositionLocalOf { true }

@Composable
fun rememberJotterHaptics(): JotterHaptics {
    val view = LocalView.current
    val isEnabled = LocalHapticEnabled.current
    return remember(view, isEnabled) { JotterHaptics(view, isEnabled) }
}

class JotterHaptics(
    private val view: android.view.View,
    private val isEnabled: Boolean
) {
    fun vibrate(type: Int) {
        if (isEnabled) {
            view.performHapticFeedback(type)
        }
    }

    // Light tap (navigation, selection)
    fun tick() = vibrate(HapticFeedbackConstants.CLOCK_TICK)
    
    // Standard click
    fun click() = vibrate(HapticFeedbackConstants.VIRTUAL_KEY)

    // Heavy impact (long press, delete)
    fun heavy() = vibrate(HapticFeedbackConstants.LONG_PRESS)

    // Success confirmation (Save) - API 30+
    fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrate(HapticFeedbackConstants.CONFIRM)
        } else {
            click()
        }
    }
    
    // Error/Reject (Validation fail) - API 30+
    fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrate(HapticFeedbackConstants.REJECT)
        } else {
            heavy()
        }
    }
}
