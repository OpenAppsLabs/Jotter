package com.openapps.jotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint  // <- import this
import com.openapps.jotter.navigation.AppNavHost
import com.openapps.jotter.ui.theme.JotterTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JotterTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
