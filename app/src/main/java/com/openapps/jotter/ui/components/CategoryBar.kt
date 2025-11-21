package com.openapps.jotter.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBar(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val displayList = listOf("All") + categories

    LaunchedEffect(selectedCategory) {
        val index = displayList.indexOf(selectedCategory)
        if (index >= 0) {
            // The custom slow animation for sliding
            val animSpec = tween<Float>(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )

            // LOGIC FIX:
            // Check if the target item is currently visible (even partially).
            // This works for Index 0 ("All") just as well as any other index!
            val layoutInfo = listState.layoutInfo
            val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }

            if (visibleItem != null) {
                // Item is on screen? Use the nice SLOW slide.
                val itemStart = visibleItem.offset
                val itemEnd = itemStart + visibleItem.size
                val viewportEnd = layoutInfo.viewportEndOffset
                val padding = with(density) { 16.dp.toPx() }

                if (itemEnd > viewportEnd) {
                    // Slide left to show item
                    listState.animateScrollBy((itemEnd - viewportEnd) + padding, animSpec)
                } else if (itemStart < 0) {
                    // Slide right to show item (This handles the "Back to All" case smoothly)
                    // Note: We use itemStart directly to align it to 0, minus padding
                    listState.animateScrollBy(itemStart.toFloat() - padding, animSpec)
                } else if (index == 0 && itemStart > 0) {
                    // Special sub-case: If "All" is visible but floating in the middle,
                    // slide it back to the absolute start (0px)
                    listState.animateScrollBy(itemStart.toFloat() - padding, animSpec)
                }
            } else {
                // Item is completely off-screen?
                // Use standard scroll. The spring is better for long distances than a tween.
                listState.animateScrollToItem(index)
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(displayList) { _, category ->
            val isSelected = category == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = {
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

        item {
            FilterChip(
                selected = false,
                onClick = onAddCategoryClick,
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