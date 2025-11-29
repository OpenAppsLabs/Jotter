package com.openapps.jotter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openapps.jotter.ui.theme.rememberJotterHaptics

@Composable
fun FAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberJotterHaptics()

    FloatingActionButton(
        onClick = {
            haptics.click()
            onClick()
        },
        modifier = modifier,
        // M3 prefers 'Container' colors for FABs to be less aggressive than M2
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Create note"
        )
    }
}
