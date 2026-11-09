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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openappslabs.jotter.data.model.AppTheme
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

private val DefaultThreeOptionHeight = 44.dp

data class ThreeOptionItem(
    val label: String,
    val icon: ImageVector? = null
)

@Composable
fun ThreeOptionButton(
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    options: List<ThreeOptionItem>,
    modifier: Modifier = Modifier,
    height: Dp = DefaultThreeOptionHeight
) {
    require(options.size == 3) { "ThreeOptionButton requires exactly 3 options" }
    val haptics = rememberJotterHaptics()
    val springSpec = remember { spring<Dp>(stiffness = 300f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        val maxWidth = maxWidth
        val count = options.size
        val spacing = 4.dp
        val outerPadding = 4.dp
        val itemWidth = (maxWidth - (outerPadding * 2) - (spacing * (count - 1))) / count

        val clampedIndex = selectedIndex.coerceIn(0, count - 1)
        val indicatorOffset by animateDpAsState(
            targetValue = outerPadding + (itemWidth + spacing) * clampedIndex,
            animationSpec = springSpec,
            label = "ThreeOptionIndicatorOffset"
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(itemWidth)
                .fillMaxHeight()
                .padding(vertical = outerPadding)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = clampedIndex == index
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    label = "ThreeOptionContentColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = {
                                if (!isSelected) {
                                    haptics.tick()
                                    onOptionSelected(index)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (option.icon != null) {
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn() + scaleIn(spring(stiffness = 300f)) + expandHorizontally(
                                    animationSpec = spring(stiffness = 300f)
                                ),
                                exit = fadeOut() + scaleOut(spring(stiffness = 300f)) + shrinkHorizontally(
                                    animationSpec = spring(stiffness = 300f)
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = option.icon,
                                        contentDescription = null,
                                        tint = contentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }

                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThreeOptionButton(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = DefaultThreeOptionHeight
) {
    val themeOptions = remember {
        listOf(
            ThreeOptionItem(label = "System", icon = Icons.Default.AutoMode),
            ThreeOptionItem(label = "Light", icon = Icons.Default.LightMode),
            ThreeOptionItem(label = "Dark", icon = Icons.Default.DarkMode)
        )
    }

    ThreeOptionButton(
        selectedIndex = selectedTheme.ordinal,
        onOptionSelected = { index ->
            onThemeSelected(AppTheme.entries[index])
        },
        options = themeOptions,
        modifier = modifier,
        height = height
    )
}
