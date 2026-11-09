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

package com.openappslabs.jotter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.openappslabs.jotter.data.model.BackupDialogType
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

private val ZeroPadding = PaddingValues(0.dp)

@Composable
fun BackupRestoreDialog(
    type: BackupDialogType,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
    val haptics = rememberJotterHaptics()

    val title = remember(type) {
        when (type) {
            BackupDialogType.NO_DATA_TO_EXPORT -> "No Data Found"
            BackupDialogType.EXPORT_SUCCESS -> "Backup Saved"
            BackupDialogType.IMPORT_SUCCESS -> "Restore Complete"
            BackupDialogType.ERROR -> "Operation Failed"
        }
    }

    val message = remember(type, errorMessage) {
        when (type) {
            BackupDialogType.NO_DATA_TO_EXPORT -> "You don't have any notes. Please create some notes before exporting."
            BackupDialogType.EXPORT_SUCCESS -> "Your notes have been successfully exported to your device."
            BackupDialogType.IMPORT_SUCCESS -> "Your notes and categories have been successfully restored."
            BackupDialogType.ERROR -> errorMessage ?: "An unknown error occurred."
        }
    }

    val icon = remember(type) {
        when (type) {
            BackupDialogType.NO_DATA_TO_EXPORT -> Icons.Default.Warning
            BackupDialogType.EXPORT_SUCCESS, BackupDialogType.IMPORT_SUCCESS -> Icons.Default.CheckCircle
            BackupDialogType.ERROR -> Icons.Default.Error
        }
    }

    val tintColor = when (type) {
        BackupDialogType.NO_DATA_TO_EXPORT -> MaterialTheme.colorScheme.tertiary
        BackupDialogType.EXPORT_SUCCESS, BackupDialogType.IMPORT_SUCCESS -> MaterialTheme.colorScheme.primary
        BackupDialogType.ERROR -> MaterialTheme.colorScheme.error
    }

    val buttonText = remember(type) {
        when (type) {
            BackupDialogType.EXPORT_SUCCESS, BackupDialogType.IMPORT_SUCCESS -> "Done"
            BackupDialogType.ERROR -> "Dismiss"
            BackupDialogType.NO_DATA_TO_EXPORT -> "Got It"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tintColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    ),
                    lineHeight = 21.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        when (type) {
                            BackupDialogType.EXPORT_SUCCESS, BackupDialogType.IMPORT_SUCCESS -> haptics.success()
                            BackupDialogType.ERROR -> haptics.heavy()
                            BackupDialogType.NO_DATA_TO_EXPORT -> haptics.click()
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tintColor.copy(alpha = 0.14f),
                        contentColor = tintColor
                    ),
                    elevation = null,
                    contentPadding = ZeroPadding
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
