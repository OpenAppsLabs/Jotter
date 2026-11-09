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

package com.openappslabs.jotter.ui.screens.datamanagementscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.data.model.BackupDialogType
import com.openappslabs.jotter.ui.components.BackupRestoreDialog
import com.openappslabs.jotter.ui.components.ConfirmationDialog
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsGroup
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemArrow
import com.openappslabs.jotter.ui.screens.settingsscreen.components.TinyGap
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    onBackClick: () -> Unit,
    viewModel: DataManagementScreenViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var activeDialog by remember { mutableStateOf<BackupDialogType?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { safeUri ->
            viewModel.exportNotes(
                onReady = { jsonString ->
                    try {
                        context.contentResolver.openOutputStream(safeUri)?.use { output ->
                            output.write(jsonString.toByteArray())
                        }
                        activeDialog = BackupDialogType.EXPORT_SUCCESS
                    } catch (e: Exception) {
                        activeDialog = BackupDialogType.ERROR
                    }
                },
                onEmpty = {
                    activeDialog = BackupDialogType.NO_DATA_TO_EXPORT
                }
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { safeUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(safeUri)
                if (inputStream != null) {
                    viewModel.importNotes(inputStream)
                }
            } catch (e: Exception) {
                activeDialog = BackupDialogType.ERROR
            }
        }
    }

    if (uiState.lastImportSuccess == true && activeDialog == null) {
        activeDialog = BackupDialogType.IMPORT_SUCCESS
        viewModel.clearError()
    }
    if (uiState.errorMessage != null && activeDialog == null) {
        activeDialog = BackupDialogType.ERROR
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Data Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick = {
                            haptics.click()
                            onBackClick()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                        icon = Icons.Default.Upload,
                        title = "Export notes",
                        subtitle = "Export notes to file",
                        onClick = {
                            haptics.click()
                            if (uiState.hasDataToExport) {
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                exportLauncher.launch("jotter_backup_$timeStamp.json")
                            } else {
                                activeDialog = BackupDialogType.NO_DATA_TO_EXPORT
                            }
                        }
                    )
                    TinyGap()
                    SettingsItemArrow(
                        icon = Icons.Default.Download,
                        title = "Import notes",
                        subtitle = "Import notes from file",
                        onClick = {
                            haptics.click()
                            importLauncher.launch("application/json")
                        }
                    )
                }
            }

            item {
                SettingsGroup {
                    SettingsItemArrow(
                        icon = Icons.Default.DeleteForever,
                        title = "Clear all local data",
                        subtitle = "Delete all data permanently",
                        onClick = { showClearAllDialog = true },
                        isDestructive = true
                    )
                }
            }

            if (uiState.isExportInProgress || uiState.isImportInProgress) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Processing...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (showClearAllDialog) {
        ConfirmationDialog(
            icon = Icons.Default.Warning,
            title = "Clear All Data",
            message = "Are you sure you want to clear all data? This action cannot be undone.",
            confirmText = "Clear All Data",
            isDestructive = true,
            onDismiss = { showClearAllDialog = false },
            onConfirm = {
                showClearAllDialog = false
                viewModel.clearAllData()
            }
        )
    }

    activeDialog?.let { type ->
        BackupRestoreDialog(
            type = type,
            onDismiss = {
                haptics.click()
                activeDialog = null
                viewModel.clearError()
            },
            errorMessage = uiState.errorMessage
        )
    }
}