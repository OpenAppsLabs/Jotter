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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@Composable
fun TwoOptionButton(
    isSecondSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    iconButtonSize: Dp = 44.dp,
    firstContent: @Composable (isSelected: Boolean) -> Unit,
    secondContent: @Composable (isSelected: Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()
    val activeContainerColor = MaterialTheme.colorScheme.secondaryContainer

    val springSpec = remember {
        spring<Dp>(
            dampingRatio = 0.8f,
            stiffness = 300f
        )
    }

    val offsetX by animateDpAsState(
        targetValue = if (isSecondSelected) iconButtonSize else 0.dp,
        animationSpec = springSpec,
        label = "TwoOptionButtonOffset"
    )

    Box(
        modifier = modifier
            .width(iconButtonSize * 2)
            .height(iconButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { translationX = offsetX.toPx() }
                .size(iconButtonSize)
                .clip(CircleShape)
                .background(activeContainerColor)
        )

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            if (isSecondSelected) {
                                haptics.tick()
                                onToggle()
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                firstContent(!isSecondSelected)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            if (!isSecondSelected) {
                                haptics.tick()
                                onToggle()
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                secondContent(isSecondSelected)
            }
        }
    }
}

@Composable
fun TwoOptionButton(
    isSecondSelected: Boolean,
    onToggle: () -> Unit,
    firstIcon: ImageVector,
    secondIcon: ImageVector,
    modifier: Modifier = Modifier,
    firstDescription: String? = null,
    secondDescription: String? = null,
    iconButtonSize: Dp = 44.dp
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    TwoOptionButton(
        isSecondSelected = isSecondSelected,
        onToggle = onToggle,
        modifier = modifier,
        iconButtonSize = iconButtonSize,
        firstContent = { isSelected ->
            Icon(
                imageVector = firstIcon,
                contentDescription = firstDescription,
                tint = if (isSelected) activeColor else inactiveColor
            )
        },
        secondContent = { isSelected ->
            Icon(
                imageVector = secondIcon,
                contentDescription = secondDescription,
                tint = if (isSelected) activeColor else inactiveColor
            )
        }
    )
}

@Composable
fun TwoOptionButton(
    isSecondSelected: Boolean,
    onToggle: () -> Unit,
    firstText: String,
    secondText: String,
    modifier: Modifier = Modifier,
    iconButtonSize: Dp = 44.dp
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    TwoOptionButton(
        isSecondSelected = isSecondSelected,
        onToggle = onToggle,
        modifier = modifier,
        iconButtonSize = iconButtonSize,
        firstContent = { isSelected ->
            Text(
                text = firstText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else inactiveColor
            )
        },
        secondContent = { isSelected ->
            Text(
                text = secondText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else inactiveColor
            )
        }
    )
}
