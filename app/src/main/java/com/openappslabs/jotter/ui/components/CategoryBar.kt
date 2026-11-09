/*
 * Copyright (c) 2026 Open Apps Labs
 *
 * This file is part of Jotter
 * * Jotter is free software: you can redistribute it and/or modify it under the terms of the
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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.openappslabs.jotter.data.model.SortDirection
import com.openappslabs.jotter.data.model.SortType
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@Immutable
data class CategoryItems(val items: List<String> = emptyList())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBar(
    modifier: Modifier = Modifier,
    categories: CategoryItems,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    showAddButton: Boolean,
    showSortButton: Boolean,
    sortDirection: SortDirection,
    sortType: SortType,
    onSortClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val haptics = rememberJotterHaptics()
    val displayList = remember(categories) { listOf("All") + categories.items }
    val scrollPaddingPx = remember(density) { with(density) { 16.dp.toPx() } }

    LaunchedEffect(selectedCategory) {
        val index = displayList.indexOf(selectedCategory)
        if (index >= 0) {
            val animSpec = tween<Float>(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )

            val layoutInfo = listState.layoutInfo
            val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }

            if (visibleItem != null) {
                val itemStart = visibleItem.offset
                val itemEnd = itemStart + visibleItem.size
                val viewportEnd = layoutInfo.viewportEndOffset

                if (itemEnd > viewportEnd) {
                    listState.animateScrollBy((itemEnd - viewportEnd) + scrollPaddingPx, animSpec)
                } else if (itemStart < 0) {
                    listState.animateScrollBy(itemStart.toFloat() - scrollPaddingPx, animSpec)
                } else if (index == 0 && itemStart > 0) {
                    listState.animateScrollBy(itemStart.toFloat() - scrollPaddingPx, animSpec)
                }
            } else {
                listState.animateScrollToItem(index)
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = if (showSortButton) 0.dp else 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(
                items = displayList,
                key = { _, category -> category }
            ) { _, category ->
                val isSelected = category == selectedCategory

                FilterChip(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        fadeOutSpec = tween(300)
                    ),
                    selected = isSelected,
                    onClick = {
                        haptics.tick()
                        if (isSelected && category != "All") {
                            onCategorySelect("All")
                        } else {
                            onCategorySelect(category)
                        }
                    },
                    label = { Text(text = category) },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            if (showAddButton) {
                item(key = "AddCategory") {
                    FilterChip(
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(300),
                            fadeOutSpec = tween(300)
                        ),
                        selected = false,
                        onClick = {
                            haptics.click()
                            onAddCategoryClick()
                        },
                        label = { Text("Add") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            enabled = true,
                            selected = false
                        )
                    )
                }
            }
        }
        if (showSortButton) {
            SortButton(
                modifier = Modifier.padding(end = 16.dp),
                onClick = onSortClick
            )
        }
    }
}