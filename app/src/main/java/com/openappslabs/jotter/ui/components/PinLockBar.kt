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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PinLockBar(
    modifier: Modifier = Modifier,
    isPinned: Boolean,
    isLocked: Boolean,
    onTogglePin: () -> Unit = {},
    onToggleLock: () -> Unit = {}
) {
    val haptics = rememberJotterHaptics()

    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = true,
        content = {
            if (isPinned) {
                FilledIconButton(
                    onClick = {
                        haptics.tick()
                        onTogglePin()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Filled.PushPin, contentDescription = "Unpin")
                }
            } else {
                IconButton(
                    onClick = {
                        haptics.tick()
                        onTogglePin()
                    }
                ) {
                    Icon(Icons.Outlined.PushPin, contentDescription = "Pin")
                }
            }

            if (isLocked) {
                FilledIconButton(
                    onClick = {
                        haptics.tick()
                        onToggleLock()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = "Unlock")
                }
            } else {
                IconButton(
                    onClick = {
                        haptics.tick()
                        onToggleLock()
                    }
                ) {
                    Icon(Icons.Outlined.LockOpen, contentDescription = "Lock")
                }
            }
        }
    )
}