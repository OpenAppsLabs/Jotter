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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DateFormats = listOf(
    "dd MMM",       // 10 Dec
    "MMM dd",       // Dec 10
    "dd/MM",        // 10/12
)
private val IconButtonSize = 44.dp
private val TotalWidth = IconButtonSize * 2
private val OuterRadius = 16.dp
private val ZeroPadding = PaddingValues(0.dp)

@Composable
fun DateFormatButton(
    currentFormat: String,
    onFormatSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptics = rememberJotterHaptics()
    val locale = Locale.getDefault()

    val exampleDates = remember(locale) {
        DateFormats.map { format ->
            try {
                SimpleDateFormat(format, locale).format(Date())
            } catch (e: Exception) {
                format
            }
        }
    }

    val currentExampleDate = remember(currentFormat, locale) {
        try {
            SimpleDateFormat(currentFormat, locale).format(Date())
        } catch (e: Exception) {
            currentFormat
        }
    }

    val buttonShapes = remember {
        val count = DateFormats.size
        DateFormats.indices.map { index ->
            when {
                count == 1 -> RoundedCornerShape(OuterRadius)
                index == 0 -> RoundedCornerShape(
                    topStart = OuterRadius, topEnd = OuterRadius,
                    bottomStart = 4.dp, bottomEnd = 4.dp
                )
                index == count - 1 -> RoundedCornerShape(
                    topStart = 4.dp, topEnd = 4.dp,
                    bottomStart = OuterRadius, bottomEnd = OuterRadius
                )
                else -> RoundedCornerShape(4.dp)
            }
        }
    }

    Box(
        modifier = modifier
            .width(TotalWidth)
            .height(IconButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = { 
                    haptics.click()
                    showDialog = true 
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = currentExampleDate,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )

        if (showDialog) {
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Date Format",
                            style = MaterialTheme.typography.titleLarge.copy(
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        DateFormats.forEachIndexed { index, format ->
                            val isSelected = format == currentFormat

                            Button(
                                onClick = {
                                    haptics.tick()
                                    onFormatSelected(format)
                                    showDialog = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = buttonShapes[index],
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
                                contentPadding = ZeroPadding
                            ) {
                                Text(
                                    text = exampleDates[index],
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    ),
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (index < DateFormats.size - 1) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}