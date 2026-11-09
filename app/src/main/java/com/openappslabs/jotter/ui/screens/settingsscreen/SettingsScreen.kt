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

package com.openappslabs.jotter.ui.screens.settingsscreen

import com.openappslabs.jotter.ui.components.BackButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsGroup
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemArrow
import com.openappslabs.jotter.ui.screens.settingsscreen.components.TinyGap
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onDataManagementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onAppearanceClick: () -> Unit,
    onTrashClick: () -> Unit,
    onMiscellaneousClick: () -> Unit,
    onSecurityClick: () -> Unit,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )
        return
    }
    
    val appBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                colors = appBarColors
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, 
                end = 16.dp, 
                top = innerPadding.calculateTopPadding() + 8.dp, 
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsGroup {
                    SettingsItemArrow(
                        icon = Icons.Default.ColorLens,
                        title = "Appearance",
                        subtitle = "Look & feel",
                        onClick = onAppearanceClick
                    )

                    TinyGap()

                    SettingsItemArrow(
                        icon = Icons.Default.Delete,
                        title = "Trash",
                        subtitle = "Notes you delete appear here",
                        onClick = onTrashClick
                    )

                    TinyGap()

                    SettingsItemArrow(
                        icon = Icons.Default.Dashboard,
                        title = "Miscellaneous",
                        subtitle = "Categories & preferences",
                        onClick = onMiscellaneousClick
                    )
                }
            }

            item {
                SettingsGroup {
                    SettingsItemArrow(
                        icon = Icons.Default.Security,
                        title = "Security",
                        subtitle = "Lock notes, prevent screenshots",
                        onClick = onSecurityClick
                    )

                    TinyGap()

                    SettingsItemArrow(
                        icon = Icons.Default.Storage,
                        title = "Data management",
                        subtitle = "Manage your data",
                        onClick = onDataManagementClick
                    )
                }
            }

            item {
                SettingsGroup {
                    SettingsItemArrow(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy policy",
                        subtitle = "How I handle your data",
                        onClick = onPrivacyPolicyClick
                    )

                    TinyGap()

                    SettingsItemArrow(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "Info & support",
                        onClick = onAboutClick
                    )
                }
            }
        }
    }
}
