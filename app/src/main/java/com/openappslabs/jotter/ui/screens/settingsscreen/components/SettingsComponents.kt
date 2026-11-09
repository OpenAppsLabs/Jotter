/*
 * Copyright (c) 2026 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 */

package com.openappslabs.jotter.ui.screens.settingsscreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openappslabs.jotter.data.model.AppTheme
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.GridView
import com.openappslabs.jotter.ui.components.DateFormatButton
import com.openappslabs.jotter.ui.components.TwoOptionButton
import com.openappslabs.jotter.ui.components.ThreeOptionButton
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import com.openappslabs.jotter.utils.AuthSupport

@Composable
fun SettingsGroup(
    title: String? = null,content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun TinyGap() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun SettingsItemBase(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
        content()
    }
}

@Composable
fun SettingsItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptics = rememberJotterHaptics()

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        checkedIconColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        uncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant
    )

    SettingsItemBase(icon = icon, title = title, subtitle = subtitle) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Switch(
                modifier = Modifier
                    .wrapContentSize()
                    .scale(0.95f),
                checked = checked,
                onCheckedChange = {
                    haptics.tick()
                    onCheckedChange(it)
                },
                colors = switchColors,
                thumbContent = {
                    val thumbIcon = if (checked) Icons.Filled.Check else Icons.Filled.Close
                    Icon(
                        imageVector = thumbIcon,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsItemGridView(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isGridView: Boolean,
    onToggle: () -> Unit
) {
    SettingsItemBase(icon = icon, title = title, subtitle = subtitle) {
        TwoOptionButton(
            isSecondSelected = isGridView,
            onToggle = onToggle,
            firstIcon = Icons.AutoMirrored.Outlined.ViewList,
            secondIcon = Icons.Outlined.GridView,
            firstDescription = "List View",
            secondDescription = "Grid View"
        )
    }
}

@Composable
fun SettingsItemArrow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val haptics = rememberJotterHaptics()
    val color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    SettingsItemBase(
        modifier = Modifier.clickable {
            haptics.click()
            onClick()
        },
        icon = icon,
        title = title,
        subtitle = subtitle,
        iconColor = color,
        titleColor = if (isDestructive) color else MaterialTheme.colorScheme.onSurface,
    ) {
        if (!isDestructive) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SettingsItemNoteLock(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    authSupport: AuthSupport,
    onCheckedChange: (Boolean) -> Unit
) {
    val icon = if (checked) {
        if (authSupport.hasFingerprint) Icons.Default.Fingerprint else Icons.Default.Lock
    } else Icons.Default.LockOpen

    SettingsItemSwitch(
        icon = icon,
        title = title,
        subtitle = subtitle,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun SettingsItemTimeFormat(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    is24Hour: Boolean,
    onToggle: (Boolean) -> Unit
) {
    SettingsItemBase(icon = icon, title = title, subtitle = subtitle) {
        TwoOptionButton(
            isSecondSelected = is24Hour,
            onToggle = { onToggle(!is24Hour) },
            firstText = "12h",
            secondText = "24h"
        )
    }
}

@Composable
fun SettingsItemDateFormat(
    icon: ImageVector,
    title: String,
    subtitle: String,
    currentFormat: String,
    onFormatSelected: (String) -> Unit
) {
    SettingsItemBase(icon = icon, title = title, subtitle = subtitle) {
        DateFormatButton(
            currentFormat = currentFormat,
            onFormatSelected = onFormatSelected
        )
    }
}

@Composable
fun SettingsItemEditView(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isEditDefault: Boolean,
    onToggleEditDefault: (Boolean) -> Unit
) {
    SettingsItemBase(icon = icon, title = title, subtitle = subtitle) {
        TwoOptionButton(
            isSecondSelected = !isEditDefault,
            onToggle = { onToggleEditDefault(!isEditDefault) },
            firstIcon = Icons.Default.Create,
            secondIcon = Icons.Default.Visibility,
            firstDescription = "Edit Mode",
            secondDescription = "View Mode"
        )
    }
}

@Composable
fun SettingsItemTheme(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ThreeOptionButton(
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected
        )
    }
}
