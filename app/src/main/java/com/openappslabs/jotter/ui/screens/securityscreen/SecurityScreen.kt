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

package com.openappslabs.jotter.ui.screens.securityscreen

import com.openappslabs.jotter.ui.components.BackButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
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
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.ui.components.ConfirmationDialog
import com.openappslabs.jotter.ui.screens.settingsscreen.SettingsScreenViewModel
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsGroup
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemNoteLock
import com.openappslabs.jotter.ui.screens.settingsscreen.components.SettingsItemSwitch
import com.openappslabs.jotter.ui.screens.settingsscreen.components.TinyGap
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import com.openappslabs.jotter.utils.BiometricAuthUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )
        return
    }

    var showClearAllDialog by remember { mutableStateOf(false) }
    var showDisableLockWarningDialog by remember { mutableStateOf(false) }

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
                        text = "Security",
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
                    val authSupport = remember(context) {
                        BiometricAuthUtil.getAuthenticationSupport(context)
                    }
                    val isBiometricAvailable = authSupport.hasFingerprint || authSupport.hasDeviceCredential

                    AnimatedVisibility(
                        visible = isBiometricAvailable,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            SettingsItemNoteLock(
                                title = "Note lock",
                                subtitle = "Require authentication to open",
                                checked = uiState.isBiometricEnabled,
                                authSupport = authSupport,
                                onCheckedChange = { isEnabled ->
                                    if (isEnabled) {
                                        viewModel.updateBiometricEnabled(true)
                                    } else {
                                        val activity = context as? FragmentActivity
                                        if (activity != null) {
                                            BiometricAuthUtil.authenticate(
                                                activity = activity,
                                                title = "Confirm Identity",
                                                subtitle = "Authenticate To Disable Note Lock",
                                                onSuccess = {
                                                    showDisableLockWarningDialog = true
                                                },
                                                onError = { }
                                            )
                                        }
                                    }
                                }
                            )
                            TinyGap()
                        }
                    }

//                    SettingsItemSwitch(
//                        icon = Icons.Default.VpnKey,
//                        title = "App Lock",
//                        subtitle = "Require authentication to open app",
//                        checked = uiState.isAppLockEnabled,
//                        onCheckedChange = { targetState ->
//                            val activity = context as? FragmentActivity
//                            if (activity != null) {
//                                val authSubtitle = if (targetState) {
//                                    "Authenticate To Enable App Lock"
//                                } else {
//                                    "Authenticate To Disable App Lock"
//                                }
//                                BiometricAuthUtil.authenticate(
//                                    activity = activity,
//                                    title = "Confirm Identity",
//                                    subtitle = authSubtitle,
//                                    onSuccess = {
//                                        viewModel.updateAppLockEnabled(targetState)
//                                    },
//                                    onError = { }
//                                )
//                            }
//                        }
//                    )
//
//                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.Security,
                        title = "Secure screen",
                        subtitle = "Disable screenshots",
                        checked = uiState.isSecureMode,
                        onCheckedChange = viewModel::updateSecureMode
                    )
                }
            }
        }

        if (showDisableLockWarningDialog) {
            ConfirmationDialog(
                icon = Icons.Default.LockOpen,
                title = "Disable Note Lock?",
                message = "This will remove the lock from all currently locked notes. They will become accessible without authentication.",
                confirmText = "Disable Lock",
                isDestructive = true,
                onDismiss = { showDisableLockWarningDialog = false },
                onConfirm = {
                    showDisableLockWarningDialog = false
                    viewModel.updateBiometricEnabled(false)
                }
            )
        }
    }
}