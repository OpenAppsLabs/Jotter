package com.openapps.jotter.ui.screens.settingsscreen

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
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Dashboard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openapps.jotter.ui.components.ClearAllDataDialog
import com.openapps.jotter.ui.components.EditViewButton
import com.openapps.jotter.ui.components.GridListButton
import com.openapps.jotter.ui.theme.rememberJotterHaptics

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

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )
        return
    }

    var showClearAllDialog by remember { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxSize().padding(top = 0.dp) // Explicitly remove top padding for Scaffold
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()), // Apply only bottom padding from innerPadding
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // --- Group 1: Appearance ---
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
                        }
                    }

                    // --- Group 2: General & Navigation ---
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

                    // --- Group 3: Security ---
//                    item {
//                        SettingsGroup(title = "Security") {
//                            SettingsItemSwitch(
//                                icon = Icons.Default.Fingerprint,
//                                title = "Biometric Unlock",
//                                subtitle = "Require fingerprint to open",
//                                checked = uiState.isBiometricEnabled,
//                                onCheckedChange = { viewModel.updateBiometricEnabled(it) }
//                            )
//                            TinyGap()
//
//                            SettingsItemSwitch(
//                                icon = Icons.Default.Security,
//                                title = "Secure Screen",
//                                subtitle = "Hide content in recent apps",
//                                checked = uiState.isSecureMode,
//                                onCheckedChange = { viewModel.updateSecureMode(it) }
//                            )
//                        }
//                    }

                    // --- Group 4: Data Management & Reset ---
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

                    // --- Group 5: About ---
                    item {
                        SettingsGroup(title = "About") {
                            SettingsItemArrow(
                                icon = Icons.Default.Lock,
                                title = "Privacy Policy",
                                onClick = onPrivacyPolicyClick
                            )

                            TinyGap()

                            SettingsItemArrow(
                                icon = Icons.Default.Info,
                                title = "Version",
                                subtitle = "1.0.0 (Alpha)",
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
            }
        }
    }
}

// --- Helper Composables ---

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
