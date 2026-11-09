/*
 * Copyright (c) 2026 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.openappslabs.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openappslabs.jotter.ui.screens.aboutscreen.AboutScreen
import com.openappslabs.jotter.ui.screens.addcategoryscreen.AddCategoryScreen
import com.openappslabs.jotter.ui.screens.appearancescreen.AppearanceScreen
import com.openappslabs.jotter.ui.screens.datamanagementscreen.DataManagementScreen
import com.openappslabs.jotter.ui.screens.miscellaneousscreen.MiscellaneousScreen
import com.openappslabs.jotter.ui.screens.homescreen.HomeScreen
import com.openappslabs.jotter.ui.screens.notedetailscreen.NoteDetailScreen
import com.openappslabs.jotter.ui.screens.privacypolicyscreen.PrivacyPolicyScreen
import com.openappslabs.jotter.ui.screens.securityscreen.SecurityScreen
import com.openappslabs.jotter.ui.screens.settingsscreen.SettingsScreen
import com.openappslabs.jotter.ui.screens.trashscreen.TrashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home,
        modifier = modifier
    ) {
        composable<AppRoutes.Home> {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(AppRoutes.NoteDetail(noteId = noteId))
                },
                onAddNoteClick = { category ->
                    navController.navigate(AppRoutes.NoteDetail(category = category))
                },
                onAddCategoryClick = { navController.navigate(AppRoutes.AddCategory) },
                onSettingsClick = { navController.navigate(AppRoutes.Settings) }
            )
        }

        composable<AppRoutes.AddCategory> {
            AddCategoryScreen(onBackClick = navController::popBackStack)
        }

        composable<AppRoutes.Settings> {
            SettingsScreen(
                onBackClick = navController::popBackStack,
                onDataManagementClick = { navController.navigate(AppRoutes.DataManagement) },
                onPrivacyPolicyClick = { navController.navigate(AppRoutes.PrivacyPolicy) },
                onAboutClick = { navController.navigate(AppRoutes.About) },
                onAppearanceClick = { navController.navigate(AppRoutes.AppearanceScreen) },
                onTrashClick = { navController.navigate(AppRoutes.Trash) },
                onMiscellaneousClick = { navController.navigate(AppRoutes.MiscellaneousScreen) },
                onSecurityClick = { navController.navigate(AppRoutes.SecurityScreen) }
            )
        }

        composable<AppRoutes.Trash> {
            TrashScreen(
                onBackClick = navController::popBackStack,
                onNoteClick = { noteId ->
                    navController.navigate(AppRoutes.NoteDetail(noteId = noteId))
                }
            )
        }

        composable<AppRoutes.DataManagement> {
            DataManagementScreen(onBackClick = navController::popBackStack)
        }

        composable<AppRoutes.PrivacyPolicy> {
            PrivacyPolicyScreen(onBackClick = navController::popBackStack)
        }

        composable<AppRoutes.About> {
            AboutScreen(onBackClick = navController::popBackStack)
        }

        composable<AppRoutes.NoteDetail> {
            NoteDetailScreen(
                onBackClick = navController::popBackStack,
                onManageCategoryClick = {
                    navController.navigate(AppRoutes.AddCategory)
                }
            )
        }

        composable<AppRoutes.AppearanceScreen> {
            AppearanceScreen(onBackClick = navController::popBackStack)
        }

        composable<AppRoutes.MiscellaneousScreen> {
            MiscellaneousScreen(
                onBackClick = { navController.popBackStack() },
                onManageTagsClick = { navController.navigate(AppRoutes.AddCategory) }
            )
        }

        composable<AppRoutes.SecurityScreen> {
            SecurityScreen(onBackClick = navController::popBackStack)
        }
    }
}
