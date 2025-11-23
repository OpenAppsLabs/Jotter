package com.openapps.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openapps.jotter.ui.screens.addcategoryscreen.AddCategoryScreen
import com.openapps.jotter.ui.screens.archivescreen.ArchiveScreen
import com.openapps.jotter.ui.screens.backuprestore.BackupRestoreScreen
import com.openapps.jotter.ui.screens.homescreen.HomeScreen
import com.openapps.jotter.ui.screens.notedetailscreen.NoteDetailScreen
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
        composable(AppRoutes.HOME) {
            HomeScreen(
                onNoteClick = { noteId ->
                    // Navigate using the consolidated detail route
                    navController.navigate("${AppRoutes.NOTE_DETAIL}/$noteId")
                },
                onAddNoteClick = {
                    // Navigate for new note, using the -1 placeholder
                    navController.navigate("${AppRoutes.NOTE_DETAIL}/-1")
                },
                onAddCategoryClick = { navController.navigate(AppRoutes.ADD_CATEGORY) },
                onSettingsClick = { navController.navigate(AppRoutes.SETTINGS) }
            )
        }

        composable(AppRoutes.ADD_CATEGORY) {
            AddCategoryScreen( onBackClick = { navController.popBackStack() } )
        }

        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onManageTagsClick = { navController.navigate(AppRoutes.ADD_CATEGORY) },
                onArchiveClick = { navController.navigate(AppRoutes.ARCHIVE) },
                onTrashClick = { navController.navigate(AppRoutes.TRASH) },
                onBackupRestoreClick = { navController.navigate(AppRoutes.BACKUP_RESTORE) }
            )
        }

        // 💡 FIX: ArchiveScreen must pass onNoteClick to navigate to the detail screen
        composable(AppRoutes.ARCHIVE) {
            ArchiveScreen(
                onBackClick = { navController.popBackStack() },
                onNoteClick = { noteId -> navController.navigate("${AppRoutes.NOTE_DETAIL}/$noteId") }
            )
        }

        composable(AppRoutes.TRASH) {
            TrashScreen(
                onBackClick = { navController.popBackStack() },
                onNoteClick = { noteId -> navController.navigate("${AppRoutes.NOTE_DETAIL}/$noteId") }
            )
        }

        composable(AppRoutes.BACKUP_RESTORE) {
            BackupRestoreScreen(onBackClick = { navController.popBackStack() })
        }

        // 💡 Consolidated Note Detail Screen: Handles viewing (ID > -1) and New Note (ID = -1)
        composable(
            route = AppRoutes.NOTE_DETAIL_ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(AppRoutes.NOTE_ID_KEY) {
                    type = NavType.IntType
                    defaultValue = -1 // Indicates a new note
                }
            )
        ) {
            // 💡 Use NoteDetailScreen
            // ViewModel handles data loading using SavedStateHandle
            NoteDetailScreen(
                onBackClick = { navController.popBackStack() },
                onManageCategoryClick = {
                    navController.navigate(AppRoutes.ADD_CATEGORY)
                }
            )
        }
    }
}
