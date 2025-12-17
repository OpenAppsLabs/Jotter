/*
 * Copyright (c) 2025 Open Apps Labs
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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.openappslabs.jotter.ui.components.ClearAllDataDialog
import com.openappslabs.jotter.ui.components.DisableLockWarningDialog
import com.openappslabs.jotter.ui.components.EditViewButton
import com.openappslabs.jotter.ui.components.GridListButton
import com.openappslabs.jotter.ui.components.TimeFormatButton
import com.openappslabs.jotter.ui.components.DateFormatButton
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import com.openappslabs.jotter.utils.AuthSupport
import com.openappslabs.jotter.utils.BiometricAuthUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onManageTagsClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onBackupRestoreClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptics = rememberJotterHaptics()

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = Color.Unspecified
                )
            )
            Scaffold(
                containerColor = Color.Transparent,
                modifier = Modifier.fillMaxSize().padding(top = 0.dp)
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SettingsGroup(title = "Appearance") {
                            SettingsItemSwitch(
                                icon = Icons.Default.DarkMode,
                                title = "Dark Theme",
                                subtitle = "Reduce eye strain",
                                checked = uiState.isDarkMode,
                                onCheckedChange = { viewModel.updateDarkMode(it) }
                            )
                            TinyGap()

                            SettingsItemSwitch(
                                icon = Icons.Default.Brightness2,
                                title = "True Black Mode",
                                subtitle = "Use pure black for OLED displays",
                                checked = uiState.isTrueBlackEnabled,
                                onCheckedChange = { viewModel.updateTrueBlackMode(it) }
                            )
                            TinyGap()

                            SettingsItemSwitch(
                                icon = Icons.Default.ColorLens,
                                title = "Dynamic Colors",
                                subtitle = "Adapt to wallpaper",
                                checked = uiState.isDynamicColor,
                                onCheckedChange = { viewModel.updateDynamicColor(it) }
                            )

                            TinyGap()
                            SettingsItemEditView(
                                icon = Icons.Default.Edit,
                                title = "Default Open Mode",
                                subtitle = "View OR Edit",
                                isEditDefault = uiState.defaultOpenInEdit,
                                onToggleEditDefault = { viewModel.updateDefaultOpenInEdit(it) }
                            )

                            TinyGap()

                            SettingsItemGridView(
                                icon = Icons.Outlined.Dashboard,
                                title = "Default View Mode",
                                subtitle = if (uiState.isGridView) "Grid View" else "List View",
                                isGridView = uiState.isGridView,
                                onToggle = {
                                    viewModel.updateGridView(!uiState.isGridView)
                                }
                            )

                            TinyGap()

                            SettingsItemTimeFormat(
                                icon = Icons.Outlined.Schedule,
                                title = "Default Time Format",
                                subtitle = if (uiState.is24HourFormat) "24 Hour Clock" else "12 Hour (AM/PM)",
                                is24Hour = uiState.is24HourFormat,
                                onToggle = { viewModel.updateTimeFormat(it) }
                            )

                            TinyGap()

                            SettingsItemDateFormat(
                                icon = Icons.Outlined.CalendarMonth,
                                title = "Date Format",
                                subtitle = "Change how dates appear",
                                currentFormat = uiState.dateFormat,
                                onFormatSelected = { newFormat ->
                                    viewModel.updateDateFormat(newFormat)
                                }
                            )
                        }
                    }

                    item {
                        SettingsGroup(title = "General & Navigation") {

                            SettingsItemArrow(
                                icon = Icons.AutoMirrored.Filled.Label,
                                title = "Manage Tags",
                                subtitle = "Add or remove note tags",
                                onClick = onManageTagsClick
                            )
                            TinyGap()

                            SettingsItemArrow(
                                icon = Icons.Default.Archive,
                                title = "Archived Notes",
                                subtitle = "Notes removed from the home screen",
                                onClick = onArchiveClick
                            )
                            TinyGap()

                            SettingsItemArrow(
                                icon = Icons.Default.Restore,
                                title = "Trash",
                                subtitle = "Permanently deleted after 7 days",
                                onClick = onTrashClick
                            )
                            TinyGap()

                            SettingsItemSwitch(
                                icon = Icons.Default.Add,
                                title = "Show Add Tag Button",
                                subtitle = "Show/Hide '+' on Home screen",
                                checked = uiState.showAddCategoryButton,
                                onCheckedChange = { viewModel.updateShowAddCategoryButton(it) }
                            )
                            TinyGap()

                            SettingsItemSwitch(
                                icon = Icons.Default.Vibration,
                                title = "Haptic Feedback",
                                subtitle = "Vibrate on touch interactions",
                                checked = uiState.isHapticEnabled,
                                onCheckedChange = { viewModel.updateHapticEnabled(it) }
                            )
                        }
                    }

                    item {
                        SettingsGroup(title = "Security") {
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
                                        title = "Note Lock",
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
                                                        subtitle = "Authenticate to disable Note Lock",
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

                            SettingsItemSwitch(
                                icon = Icons.Default.Security,
                                title = "Secure Screen",
                                subtitle = "Disable screenshots",
                                checked = uiState.isSecureMode,
                                onCheckedChange = { viewModel.updateSecureMode(it) }
                            )
                        }
                    }

                    item {
                        SettingsGroup(title = "Data Management") {
                            SettingsItemArrow(
                                icon = Icons.Default.Backup,
                                title = "Backup & Restore",
                                subtitle = "Export or import notes",
                                onClick = onBackupRestoreClick
                            )
                            TinyGap()

                            SettingsItemArrow(
                                icon = Icons.Default.DeleteForever,
                                title = "Clear All Local Data",
                                subtitle = "Reset app and delete all notes permanently",
                                onClick = { showClearAllDialog = true },
                                isDestructive = true
                            )
                        }
                    }

                    item {
                        SettingsGroup(title = "About") {
                            SettingsItemArrow(
                                icon = Icons.Default.PrivacyTip,
                                title = "Privacy Policy",
                                onClick = onPrivacyPolicyClick
                            )

                            TinyGap()

                            SettingsItemArrow(
                                icon = Icons.Default.Info,
                                title = "Version",
                                subtitle = com.openappslabs.jotter.BuildConfig.VERSION_NAME,
                                onClick = onAboutClick
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }

                if (showClearAllDialog) {
                    ClearAllDataDialog(
                        onDismiss = { showClearAllDialog = false },
                        onConfirm = {
                            showClearAllDialog = false
                            viewModel.clearAllData()
                        }
                    )
                }

                if (showDisableLockWarningDialog) {
                    DisableLockWarningDialog(
                        onDismiss = { showDisableLockWarningDialog = false },
                        onConfirm = {
                            showDisableLockWarningDialog = false
                            viewModel.updateBiometricEnabled(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TinyGap() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {}
}


@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 4.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = {
                haptics.tick()
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedIconColor = MaterialTheme.colorScheme.onBackground,
                uncheckedThumbColor = MaterialTheme.colorScheme.background,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            thumbContent = {
                val icon = if (checked) Icons.Filled.Check else Icons.Filled.Close
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        )
    }
}

@Composable
fun SettingsItemNoteLock(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    authSupport: AuthSupport,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()
    val icon = if (checked) {
        if (authSupport.hasFingerprint) Icons.Default.Fingerprint else Icons.Default.Lock
    } else {
        Icons.Default.Lock
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = {
                    haptics.tick()
                    onCheckedChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.background,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedIconColor = MaterialTheme.colorScheme.onBackground,
                    uncheckedThumbColor = MaterialTheme.colorScheme.background,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                    uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                thumbContent = {
                    val icon = if (checked) Icons.Filled.Check else Icons.Filled.Close
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsItemEditView(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isEditDefault: Boolean,
    onToggleEditDefault: (Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        EditViewButton(
            isEditing = isEditDefault,
            onToggle = {
                haptics.tick()
                onToggleEditDefault(!isEditDefault)
            }
        )
    }
}

@Composable
fun SettingsItemGridView(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isGridView: Boolean,
    onToggle: () -> Unit
) {
    val haptics = rememberJotterHaptics()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        GridListButton(
            isGridView = isGridView,
            onToggle = {
                haptics.tick()
                onToggle()
            }
        )
    }
}

@Composable
fun SettingsItemArrow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val haptics = rememberJotterHaptics()

    val titleColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = {
                haptics.click()
                onClick()
            })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (!isDestructive) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SettingsItemTimeFormat(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    is24Hour: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TimeFormatButton(
            is24Hour = is24Hour,
            onToggle = {
                haptics.tick()
                onToggle(!is24Hour)
            }
        )
    }
}

@Composable
fun SettingsItemDateFormat(
    icon: ImageVector,
    title: String,
    currentFormat: String,
    onFormatSelected: (String) -> Unit,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DateFormatButton(
            currentFormat = currentFormat,
            onFormatSelected = onFormatSelected
        )
    }
}