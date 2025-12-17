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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateFormatButton(
    currentFormat: String,
    onFormatSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val activeContentColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val iconButtonSize = 48.dp
    val totalWidth = iconButtonSize * 2 // 96dp

    val dateFormats = listOf(
        "dd MMM",       // 10 Dec
        "MMM dd",       // Dec 10
        "dd/MM",        // 10/12
    )

    fun getExampleDate(format: String): String {
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.format(Date())
        } catch (e: Exception) {
            format
        }
    }

    Box(
        modifier = modifier
            .width(totalWidth)
            .height(iconButtonSize)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = { showDialog = true }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getExampleDate(currentFormat),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = activeContentColor,
            maxLines = 1
        )

        if (showDialog) {
            val outerRadius = 25.dp
            val zeroPadding = PaddingValues(0.dp)
            val horizontalPadding = 24.dp

            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = horizontalPadding, end = horizontalPadding, top = 24.dp)
                    ) {
                        Text(
                            text = "Date Format",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .clickable { showDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = horizontalPadding,
                                end = horizontalPadding,
                                bottom = 24.dp,
                                top = 0.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        val itemCount = dateFormats.size

                        dateFormats.forEachIndexed { index, format ->
                            val isSelected = format == currentFormat
                            val exampleText = getExampleDate(format)
                            val shape = when {
                                itemCount == 1 -> RoundedCornerShape(outerRadius)
                                index == 0 -> RoundedCornerShape(
                                    topStart = outerRadius, topEnd = outerRadius,
                                    bottomStart = 4.dp, bottomEnd = 4.dp
                                )
                                index == itemCount - 1 -> RoundedCornerShape(
                                    topStart = 4.dp, topEnd = 4.dp,
                                    bottomStart = outerRadius, bottomEnd = outerRadius
                                )
                                else -> RoundedCornerShape(4.dp)
                            }

                            Button(
                                onClick = {
                                    onFormatSelected(format)
                                    showDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = shape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = if (isSelected)
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                ),
                                elevation = null,
                                contentPadding = zeroPadding
                            ) {
                                Text(
                                    text = exampleText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                )
                            }

                            if (index < itemCount - 1) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}