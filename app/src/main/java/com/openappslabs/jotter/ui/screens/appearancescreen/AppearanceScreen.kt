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

package com.openappslabs.jotter.ui.screens.appearancescreen

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
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.ui.screens.settingsscreen.SettingsScreenViewModel
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsGroup
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemDateFormat
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemEditView
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemGridView
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemSwitch
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemTheme
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemTimeFormat
import com.openappslabs.jotter.ui.screens.settingsscreen.components.TinyGap
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBackClick: () -> Unit,
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
                        text = "Appearance",
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
                    SettingsItemTheme(
                        icon = Icons.Default.DarkMode,
                        title = "App theme",
                        subtitle = "Select app appearance",
                        selectedTheme = uiState.appTheme,
                        onThemeSelected = viewModel::updateAppTheme
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.Brightness2,
                        title = "True dark mode",
                        subtitle = "Pure black for OLED displays",
                        checked = uiState.isTrueBlackEnabled,
                        onCheckedChange = viewModel::updateTrueBlackMode
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.ColorLens,
                        title = "Dynamic colors",
                        subtitle = "Adapt to wallpaper",
                        checked = uiState.isDynamicColor,
                        onCheckedChange = viewModel::updateDynamicColor
                    )
                }
            }
            item {
                SettingsGroup {
                    SettingsItemEditView(
                        icon = Icons.Default.Edit,
                        title = "Default open mode",
                        subtitle = "View or edit",
                        isEditDefault = uiState.defaultOpenInEdit,
                        onToggleEditDefault = viewModel::updateDefaultOpenInEdit
                    )

                    TinyGap()

                    SettingsItemGridView(
                        icon = Icons.Outlined.Dashboard,
                        title = "Default view mode",
                        subtitle = if (uiState.isGridView) "Grid view" else "List view",
                        isGridView = uiState.isGridView,
                        onToggle = {
                            viewModel.updateGridView(!uiState.isGridView)
                        }
                    )
                }
            }
            item {
                SettingsGroup {
                    SettingsItemTimeFormat(
                        icon = Icons.Outlined.Schedule,
                        title = "Default time format",
                        subtitle = if (uiState.is24HourFormat) "24‑hour clock" else "12 hour (AM/PM)",
                        is24Hour = uiState.is24HourFormat,
                        onToggle = viewModel::updateTimeFormat
                    )

                    TinyGap()

                    SettingsItemDateFormat(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "Date format",
                        subtitle = "Change how dates appear",
                        currentFormat = uiState.dateFormat,
                        onFormatSelected = viewModel::updateDateFormat
                    )
                }
            }
        }
    }
}