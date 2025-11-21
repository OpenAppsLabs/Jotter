package com.openapps.jotter.ui.screens.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openapps.jotter.ui.components.Header
import com.openapps.jotter.ui.components.ClearAllDataDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onManageTagsClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onBackupRestoreClick: () -> Unit // Add this
) {
    // Dummy state
    var isDarkMode by remember { mutableStateOf(false) }
    var isDynamicColor by remember { mutableStateOf(true) }
    var isBiometricEnabled by remember { mutableStateOf(false) }
    var isSecureMode by remember { mutableStateOf(false) }
    var isHapticEnabled by remember { mutableStateOf(true) }
    var isTrueBlackEnabled by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val trashCount = 3

    Scaffold(
        topBar = {
            // REUSED HEADER: Consistent typography and back button logic
            Header(
                title = "Settings",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it }
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.Brightness2,
                        title = "True Black Mode",
                        subtitle = "Use pure black for OLED displays",
                        checked = isTrueBlackEnabled,
                        onCheckedChange = { isTrueBlackEnabled = it }
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.ColorLens,
                        title = "Dynamic Colors",
                        subtitle = "Adapt to wallpaper",
                        checked = isDynamicColor,
                        onCheckedChange = { isDynamicColor = it }
                    )
                }
            }

            // --- Group 2: General & Navigation ---
            item {
                SettingsGroup(title = "General & Navigation") {
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

                    SettingsItemArrow(
                        icon = Icons.AutoMirrored.Filled.Label,
                        title = "Manage Tags",
                        subtitle = "Add, edit, or remove note tags",
                        onClick = onManageTagsClick
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Vibrate on touch interactions",
                        checked = isHapticEnabled,
                        onCheckedChange = { isHapticEnabled = it }
                    )
                }
            }

            // --- Group 3: Security ---
            item {
                SettingsGroup(title = "Security") {
                    SettingsItemSwitch(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometric Unlock",
                        subtitle = "Require fingerprint to open",
                        checked = isBiometricEnabled,
                        onCheckedChange = { isBiometricEnabled = it }
                    )
                    TinyGap()

                    SettingsItemSwitch(
                        icon = Icons.Default.Security,
                        title = "Secure Screen",
                        subtitle = "Hide content in recent apps",
                        checked = isSecureMode,
                        onCheckedChange = { isSecureMode = it }
                    )
                }
            }

            // --- Group 4: Data Management & Reset ---
            item {
                SettingsGroup(title = "Data Management") {
                    SettingsItemArrow(
                        icon = Icons.Default.Backup,
                        title = "Backup & Restore",
                        subtitle = "Export or import notes",
                        onClick = onBackupRestoreClick // Connect it here
                    )
                    TinyGap()

                    // DESTRUCTIVE ACTION
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
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Alpha)",
                        onClick = { }
                    )
                    TinyGap()

                    SettingsItemArrow(
                        icon = Icons.Default.Lock,
                        title = "Privacy Policy",
                        onClick = { }
                    )
                }
            }
            // --- FINAL SPACER ---
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        // After LazyColumn
        if (showClearAllDialog) {
            ClearAllDataDialog(
                onDismiss = { showClearAllDialog = false },
                onConfirm = {
                    showClearAllDialog = false
                    // TODO: clear all data logic here
                }
            )
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
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.background,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
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
    val titleColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick)
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