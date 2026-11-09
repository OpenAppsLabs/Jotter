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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openappslabs.jotter.R
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import kotlinx.coroutines.delay

private var isFirstLaunch = true

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search Notes"
) {
    val haptics = rememberJotterHaptics()
    val focusManager = LocalFocusManager.current
    val isKeyboardVisible = WindowInsets.isImeVisible
    var activePlaceholder by remember { mutableStateOf(if (isFirstLaunch) "Jotter" else placeholder) }
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    val placeholderStyle = remember {
        TextStyle(
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
    val textFieldStyle = remember(onSurfaceColor) {
        TextStyle(
            fontSize = 16.sp,
            color = onSurfaceColor,
            textAlign = TextAlign.Start
        )
    }
    val cursorBrush = remember(primaryColor) {
        SolidColor(primaryColor)
    }

    val transitionSpec = remember {
        (slideInVertically { height -> height } + fadeIn()) togetherWith
                (slideOutVertically { height -> -height } + fadeOut())
    }

    val keyboardOptions = remember { KeyboardOptions(imeAction = ImeAction.Search) }
    val keyboardActions = remember { KeyboardActions(onSearch = { focusManager.clearFocus() }) }

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        if (isFirstLaunch) {
            delay(2000)
            activePlaceholder = placeholder
            isFirstLaunch = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Crossfade(
                targetState = query.isNotEmpty(),
                label = "LeftIcon"
            ) { hasQuery ->
                if (hasQuery) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = onSurfaceVariantColor,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    IconButton(onClick = {
                        haptics.click()
                        onProfileClick()
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon),
                            contentDescription = "App Icon",
                            colorFilter = ColorFilter.tint(onSurfaceVariantColor),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (query.isEmpty()) {
                    AnimatedContent(
                        targetState = activePlaceholder,
                        transitionSpec = { transitionSpec },
                        label = "PlaceholderAnimation"
                    ) { targetText ->
                        Text(
                            text = targetText,
                            style = placeholderStyle.copy(color = onSurfaceVariantColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = textFieldStyle,
                    singleLine = true,
                    cursorBrush = cursorBrush,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Crossfade(
                targetState = query.isNotEmpty(),
                label = "RightIcon"
            ) { hasQuery ->
                if (hasQuery) {
                    IconButton(
                        onClick = {
                            haptics.tick()
                            onQueryChange("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear search",
                            tint = onSurfaceVariantColor
                        )
                    }
                } else {
                    IconButton(onClick = {
                        haptics.click()
                        onSettingsClick()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = onSurfaceVariantColor
                        )
                    }
                }
            }
        }
    }
}