package com.openappslabs.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openappslabs.jotter.ui.screens.aboutscreen.AboutScreen
import com.openappslabs.jotter.ui.screens.addcategoryscreen.AddCategoryScreen
import com.openappslabs.jotter.ui.screens.archivescreen.ArchiveScreen
import com.openappslabs.jotter.ui.screens.backuprestore.BackupRestoreScreen
import com.openappslabs.jotter.ui.screens.homescreen.HomeScreen
import com.openappslabs.jotter.ui.screens.notedetailscreen.NoteDetailScreen
import com.openappslabs.jotter.ui.screens.privacypolicyscreen.PrivacyPolicyScreen
import com.openappslabs.jotter.ui.screens.settingsscreen.SettingsScreen
import com.openappslabs.jotter.ui.screens.trashscreen.TrashScreen

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
                    navController.navigate("${AppRoutes.NOTE_DETAIL}/$noteId")
                },
                onAddNoteClick = {
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
                onBackupRestoreClick = { navController.navigate(AppRoutes.BACKUP_RESTORE) },
                onPrivacyPolicyClick = { navController.navigate(AppRoutes.PRIVACY_POLICY) },
                onAboutClick = { navController.navigate(AppRoutes.ABOUT) }
                // Note: onLaunchBiometricPrompt needs to be added here from MainActivity
                // when implementing app lock.
            )
        }

        composable(AppRoutes.ARCHIVE) {
            ArchiveScreen(
                onBackClick = { navController.popBackStack() },
                onNoteClick = { noteId -> navController.navigate("${AppRoutes.NOTE_DETAIL}/$noteId") }
                // Note: onRestoreComplete callback needs to be added here.
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

        composable(AppRoutes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
        }

        composable(AppRoutes.ABOUT) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }

        // 💡 Consolidated Note Detail Screen:
        composable(
            route = AppRoutes.NOTE_DETAIL_ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(AppRoutes.NOTE_ID_KEY) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            // ✨ FIX: Provide all the newly required navigation callbacks
            NoteDetailScreen(
                onBackClick = { navController.popBackStack() },
                onManageCategoryClick = {
                    navController.navigate(AppRoutes.ADD_CATEGORY)
                },
                // Mappings for actions that change a note's state and require navigation:
                onNavigateToArchive = { navController.navigate(AppRoutes.ARCHIVE) },
                onNavigateToTrash = { navController.navigate(AppRoutes.TRASH) },
                onNavigateToHome = { navController.navigate(AppRoutes.HOME) }
            )
        }
    }
}