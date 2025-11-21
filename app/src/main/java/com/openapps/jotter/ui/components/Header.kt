package com.openapps.jotter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    // Optional: Only used for Home Screen
    isGridView: Boolean = false,
    onToggleView: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    // Optional: Only used for Detail Screens
    onBackClick: (() -> Unit)? = null,
    // Save Action
    onSaveClick: (() -> Unit)? = null,
    isSaveEnabled: Boolean = false,
    // ✨ NEW PARAMETER: Delete Action
    onDeleteClick: (() -> Unit)? = null,
    // Edit/View Toggle
    isEditing: Boolean = false,
    onToggleEditView: (() -> Unit)? = null
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    if (onBackClick != null) {
        // --- DETAIL SCREEN MODE ---
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                if (onToggleEditView != null) {
                    EditViewButton(
                        isEditing = isEditing,
                        onToggle = onToggleEditView
                    )
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            navigationIcon = {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.padding(start = 16.dp).size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSaveEnabled) Icons.Default.Close else Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (isSaveEnabled) "Close" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            actions = {
                // ✨ Logic: Show Delete if provided (View Mode), otherwise show Save (Edit Mode)
                if (onDeleteClick != null) {
                    Surface(
                        onClick = onDeleteClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(end = 16.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error, // Red for delete
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else if (onSaveClick != null) {
                    Surface(
                        onClick = onSaveClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        enabled = isSaveEnabled,
                        modifier = Modifier.padding(end = 16.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Save",
                                tint = if (isSaveEnabled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            },
            colors = colors
        )
    } else {
        // --- HOME SCREEN MODE ---
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            actions = {
                if (onToggleView != null) {
                    FilledTonalIconButton(onClick = onToggleView) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView,
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (onSettingsClick != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalIconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            },
            colors = colors
        )
    }
}