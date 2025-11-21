package com.openapps.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.screens.addcategoryscreen.AddCategoryScreen
import com.openapps.jotter.ui.screens.archivescreen.ArchiveScreen
import com.openapps.jotter.ui.screens.backuprestore.BackupRestoreScreen
import com.openapps.jotter.ui.screens.homescreen.HomeScreen
import com.openapps.jotter.ui.screens.settingsscreen.SettingsScreen
import com.openapps.jotter.ui.screens.trashscreen.TrashScreen
// 💡 IMPORTANT: Use the correct, final screen name and package
import com.openapps.jotter.ui.screens.notedetailscreen.NoteDetailScreen

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

        composable(AppRoutes.ARCHIVE) {
            ArchiveScreen(onBackClick = { navController.popBackStack() })
        }

        composable(AppRoutes.TRASH) {
            TrashScreen(onBackClick = { navController.popBackStack() })
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
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt(AppRoutes.NOTE_ID_KEY)

            // --- MOCK DATA LOOKUP (CRITICAL) ---
            val noteToView = remember(noteId) {
                sampleNotes.find { it.id == noteId }
            }

            // Only pass the ID if it's an existing note
            val noteIdArg = noteId?.takeIf { it != -1 }

            // 💡 Use NoteDetailScreen
            NoteDetailScreen(
                noteId = noteIdArg,
                initialTitle = noteToView?.title ?: "",
                initialContent = noteToView?.content ?: "",

                // ✨ NEW: Pass Metadata Fields
                category = noteToView?.category ?: "Uncategorized",
                isPinned = noteToView?.isPinned ?: false,
                isLocked = noteToView?.isLocked ?: false,
                isArchived = noteToView?.isArchived ?: false,
                isTrashed = noteToView?.isTrashed ?: false,
                lastEdited = noteToView?.updatedTime ?: System.currentTimeMillis(),

                onBackClick = { navController.popBackStack() },
                onSave = { title, content ->
                    // TODO: Mock save/update logic here
                    println("MOCK SAVE: Note ID $noteIdArg saved with title: $title")
                }
            )
        }
    }
}