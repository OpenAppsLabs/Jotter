package com.openappslabs.jotter

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.openappslabs.jotter.data.repository.UserPreferencesRepository
import com.openappslabs.jotter.navigation.AppNavHost
import com.openappslabs.jotter.ui.theme.JotterTheme
import com.openappslabs.jotter.ui.theme.LocalHapticEnabled
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(initial = com.openappslabs.jotter.data.repository.UserPreferences())

            LaunchedEffect(userPreferences.isSecureMode) {
                if (userPreferences.isSecureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            JotterTheme(
                isDarkTheme = userPreferences.isDarkMode,
                isTrueBlackEnabled = userPreferences.isTrueBlackEnabled,
                isDynamicColor = userPreferences.isDynamicColor
            ) {
                CompositionLocalProvider(LocalHapticEnabled provides userPreferences.isHapticEnabled) {
                    val navController = rememberNavController()
                        AppNavHost(navController = navController)
                }
            }
        }
    }
}
