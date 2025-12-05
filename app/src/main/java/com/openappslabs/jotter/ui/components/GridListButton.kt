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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun GridListButton(
    isGridView: Boolean, // Renamed parameter to match usage
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeContentColor = MaterialTheme.colorScheme.primary
    val inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val activeContainerColor = MaterialTheme.colorScheme.secondaryContainer

    val iconButtonSize = 48.dp

    // If List view (!isGridView), offset is 0 (Left). If Grid view, offset is size (Right).
    val offsetX by animateDpAsState(
        targetValue = if (!isGridView) 0.dp else iconButtonSize,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "SlidingIndicatorOffset"
    )

    Box(
        modifier = modifier
            .width(iconButtonSize * 2)
            .height(iconButtonSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        // 1. The Sliding Indicator (The animated background)
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .size(iconButtonSize)
                .clip(CircleShape)
                .background(activeContainerColor)
        )

        // 2. The Buttons/Icons laid out in a Row on top of the indicator
        Row(Modifier.fillMaxSize()) {
            // List Button Area (Left)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { if (isGridView) onToggle() } // Click to switch to List
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ViewList,
                    contentDescription = "List View",
                    tint = if (!isGridView) activeContentColor else inactiveContentColor
                )
            }

            // Grid Button Area (Right)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { if (!isGridView) onToggle() } // Click to switch to Grid
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.GridView,
                    contentDescription = "Grid View",
                    tint = if (isGridView) activeContentColor else inactiveContentColor
                )
            }
        }
    }
}