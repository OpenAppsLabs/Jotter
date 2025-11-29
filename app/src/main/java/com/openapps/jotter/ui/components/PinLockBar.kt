package com.openapps.jotter.ui.components

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
import com.openapps.jotter.ui.theme.rememberJotterHaptics

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
            // --- Pin Toggle ---
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

            // --- Lock Toggle ---
            if (isLocked) {
                FilledIconButton(
                    onClick = {
                        haptics.tick()
                        onToggleLock()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        // ✨ UPDATED: Use Error color for Locked state
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
