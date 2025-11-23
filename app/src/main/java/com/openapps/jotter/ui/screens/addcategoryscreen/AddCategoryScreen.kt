package com.openapps.jotter.ui.screens.addcategoryscreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel  // <-- UPDATED IMPORT
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onBackClick: () -> Unit,
    viewModel: AddCategoryScreenViewModel = hiltViewModel()   // <-- uses the new import
) {
    var newCategory by remember { mutableStateOf("") }

    // Observe categories from ViewModel
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val maxCharLimit = 15

    // Setup Lazy Grid State
    val lazyGridState = rememberLazyStaggeredGridState()

    // Setup Reorderable State
    val reorderableState = rememberReorderableLazyStaggeredGridState(lazyGridState) { from, to ->
        val fromIndex = from.index - 2
        val toIndex   = to.index   - 2

        viewModel.reorderCategories(fromIndex, toIndex)
    }

    val transparentColors = TextFieldDefaults.colors(
        focusedContainerColor    = Color.Transparent,
        unfocusedContainerColor  = Color.Transparent,
        focusedIndicatorColor    = Color.Transparent,
        unfocusedIndicatorColor  = Color.Transparent,
        cursorColor              = MaterialTheme.colorScheme.onSurface,
        focusedPlaceholderColor  = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        unfocusedPlaceholderColor= MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text       = "Manage Tags",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick   = onBackClick,
                        shape     = CircleShape,
                        color     = MaterialTheme.colorScheme.surfaceContainer,
                        modifier  = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector    = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint            = MaterialTheme.colorScheme.onSurface,
                                modifier        = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            state                = lazyGridState,
            columns              = StaggeredGridCells.Fixed(2),
            modifier             = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    Text(
                        text  = "Create tags to organize your notes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                RoundedCornerShape(12.dp)
                            )
                            .height(60.dp)
                    ) {
                        TextField(
                            value       = newCategory,
                            onValueChange= {
                                if (it.length <= maxCharLimit) newCategory = it
                            },
                            placeholder  = { Text("New tag name...") },
                            textStyle    = MaterialTheme.typography.bodyLarge,
                            colors       = transparentColors,
                            singleLine   = true,
                            modifier     = Modifier
                                .weight(1f)
                                .padding(bottom = 4.dp, start = 8.dp)
                        )

                        Box(
                            modifier        = Modifier.padding(end = 6.dp),
                            contentAlignment= Alignment.Center
                        ) {
                            Surface(
                                onClick   = {
                                    viewModel.addCategory(newCategory)
                                    if (newCategory.isNotBlank()) newCategory = ""
                                },
                                shape     = RoundedCornerShape(12.dp),
                                color     = MaterialTheme.colorScheme.primary,
                                contentColor= MaterialTheme.colorScheme.onPrimary,
                                modifier  = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector     = Icons.Default.Add,
                                        contentDescription= "Add",
                                        modifier        = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text      = "${newCategory.length} / $maxCharLimit",
                        style     = MaterialTheme.typography.labelSmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text         = "EXISTING TAGS (Long press to reorder)",
                    style        = MaterialTheme.typography.labelMedium,
                    fontWeight   = FontWeight.Bold,
                    color        = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing= 1.sp,
                    modifier     = Modifier.padding(bottom = 4.dp)
                )
            }

            items(categories, key = { it }) { category ->
                val itemIndex = categories.indexOf(category) + 1

                ReorderableItem(reorderableState, key = category) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "drag")

                    Box(modifier = Modifier.longPressDraggableHandle()) {
                        CategoryChip(
                            text       = category,
                            elevation  = elevation,
                            onDelete   = { viewModel.removeCategory(category) },
                            isDragging = isDragging,
                            itemIndex  = itemIndex
                        )
                    }
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(
    text       : String,
    elevation  : androidx.compose.ui.unit.Dp = 0.dp,
    onDelete   : () -> Unit,
    isDragging : Boolean = false,
    itemIndex  : Int     = 0
) {
    Surface(
        color         = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape         = RoundedCornerShape(8.dp),
        border        = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier      = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shadowElevation= elevation
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(1f)
            ) {
                Box(
                    modifier         = Modifier.width(46.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isDragging) {
                        Text(
                            text       = "$itemIndex.",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary,
                            modifier   = Modifier.padding(start = 16.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxSize())
                    }
                }

                Text(
                    text      = text,
                    style     = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    modifier  = Modifier.weight(1f, fill = false)
                )
            }

            IconButton(
                onClick  = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    imageVector     = Icons.Default.Close,
                    contentDescription= "Remove",
                    tint            = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier        = Modifier.size(16.dp)
                )
            }
        }
    }
}