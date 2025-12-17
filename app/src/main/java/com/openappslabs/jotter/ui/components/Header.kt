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

package com.openappslabs.jotter.ui.components

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
import androidx.compose.material.icons.filled.Restore
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    isGridView: Boolean = false,
    onToggleView: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    isSaveEnabled: Boolean = false,
    onDeleteClick: (() -> Unit)? = null,
    onRestoreClick: (() -> Unit)? = null,
    isEditing: Boolean = false,
    onToggleEditView: (() -> Unit)? = null,
    onActionIcon: ImageVector = Icons.Filled.Done
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    if (onBackClick != null) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                if (onToggleEditView != null) {
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
                    modifier = Modifier.padding(start = 12.dp).size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val showCloseIcon = isEditing || isSaveEnabled

                        Icon(
                            imageVector = if (showCloseIcon) Icons.Default.Close else Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (showCloseIcon) "Close" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            actions = {
                if (onRestoreClick != null) {
                    Surface(
                        onClick = onRestoreClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(end = 12.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Restore/Unarchive",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else if (onDeleteClick != null) {
                    Surface(
                        onClick = onDeleteClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(end = 12.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
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
                        modifier = Modifier.padding(end = 12.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = onActionIcon,
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
                            imageVector = (if (isGridView) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView),
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