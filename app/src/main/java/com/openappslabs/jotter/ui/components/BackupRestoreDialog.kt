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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class BackupDialogType {
    NO_DATA_TO_EXPORT,
    EXPORT_SUCCESS,
    IMPORT_SUCCESS,
    ERROR
}

private val DialogOuterRadius = 12.dp
private val ZeroPadding = PaddingValues(0.dp)

@Composable
fun BackupRestoreDialog(
    type: BackupDialogType,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
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

    val iconColor = when (type) {
        BackupDialogType.NO_DATA_TO_EXPORT -> MaterialTheme.colorScheme.tertiary
        BackupDialogType.EXPORT_SUCCESS, BackupDialogType.IMPORT_SUCCESS -> MaterialTheme.colorScheme.primary
        BackupDialogType.ERROR -> MaterialTheme.colorScheme.error
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
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(DialogOuterRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null,
                    contentPadding = ZeroPadding
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}