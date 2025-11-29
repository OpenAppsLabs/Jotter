package com.openapps.jotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.openapps.jotter.data.repository.UserPreferencesRepository
import com.openapps.jotter.navigation.AppNavHost
import com.openapps.jotter.ui.theme.JotterTheme
import com.openapps.jotter.ui.theme.LocalHapticEnabled
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(initial = com.openapps.jotter.data.repository.UserPreferences())

            JotterTheme(
                isDarkTheme = userPreferences.isDarkMode,
                isTrueBlackEnabled = userPreferences.isTrueBlackEnabled,
                isDynamicColor = userPreferences.isDynamicColor
            ) {
                // Provide the haptic preference globally
                CompositionLocalProvider(LocalHapticEnabled provides userPreferences.isHapticEnabled) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
