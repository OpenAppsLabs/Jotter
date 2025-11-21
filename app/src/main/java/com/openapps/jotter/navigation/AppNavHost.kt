package com.openapps.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openapps.jotter.ui.screens.addcategoryscreen.AddCategoryScreen
import com.openapps.jotter.ui.screens.archivescreen.ArchiveScreen // Make sure to import this
import com.openapps.jotter.ui.screens.backuprestore.BackupRestoreScreen
import com.openapps.jotter.ui.screens.homescreen.HomeScreen
import com.openapps.jotter.ui.screens.settingsscreen.SettingsScreen
import com.openapps.jotter.ui.screens.trashscreen.TrashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = modifier
    ) {
        // Route: HOME
        composable(AppRoutes.HOME) {
            HomeScreen(
                onAddCategoryClick = {
                    navController.navigate(AppRoutes.ADD_CATEGORY)
                },
                onSettingsClick = {
                    navController.navigate(AppRoutes.SETTINGS)
                }
            )
        }

        // Route: ADD CATEGORY
        composable(AppRoutes.ADD_CATEGORY) {
            AddCategoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Route: SETTINGS
        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onManageTagsClick = {
                    navController.navigate(AppRoutes.ADD_CATEGORY)
                },
                // CONNECTED: Now navigates to Archive
                onArchiveClick = {
                    navController.navigate(AppRoutes.ARCHIVE)
                },
                onTrashClick = { navController.navigate(AppRoutes.TRASH) },
                onBackupRestoreClick = { navController.navigate(AppRoutes.BACKUP_RESTORE) }
            )
        }

        // Route: ARCHIVE (New)
        composable(AppRoutes.ARCHIVE) {
            ArchiveScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 2. Add Trash Route
        composable(AppRoutes.TRASH) {
            TrashScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.BACKUP_RESTORE) {
            BackupRestoreScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}