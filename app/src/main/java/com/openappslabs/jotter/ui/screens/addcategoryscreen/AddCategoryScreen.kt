package com.openappslabs.jotter.ui.screens.addcategoryscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// NOTE: All reorderable imports have been removed.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onBackClick: () -> Unit,
    viewModel: AddCategoryScreenViewModel = hiltViewModel()
) {
    var newCategory by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Clear focus when IME is dismissed (e.g. via keyboard down button)
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    // Intercept back press to clear focus immediately
    BackHandler(enabled = isImeVisible) {
        focusManager.clearFocus()
    }

    // Observe categories from ViewModel
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val maxCharLimit = 15

    // NOTE: LazyGridState and ReorderableState removed.

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
            columns              = StaggeredGridCells.Fixed(2),
            modifier             = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
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
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            modifier     = Modifier
                                .weight(1f)
                                .padding(bottom = 4.dp, start = 4.dp)
                        )

                        Box(
                            modifier        = Modifier.padding(end = 6.dp),
                            contentAlignment= Alignment.Center
                        ) {
                            Surface(
                                onClick   = {
                                    viewModel.addCategory(newCategory)
                                    if (newCategory.isNotBlank()) {
                                        newCategory = ""
                                        focusManager.clearFocus()
                                    }
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
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text         = "EXISTING TAGS", // Removed "Alphabetically Sorted"
                    style        = MaterialTheme.typography.labelMedium,
                    fontWeight   = FontWeight.Bold,
                    color        = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing= 1.sp,
                    modifier     = Modifier.padding(bottom = 4.dp)
                )
            }

            // CategoryChip rendering is now simplified
            items(categories, key = { it }) { category ->
                CategoryChip(
                    modifier   = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        fadeOutSpec = tween(300)
                    ),
                    text       = category,
                    elevation  = 0.dp,
                    onDelete   = { viewModel.removeCategory(category) },
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(
    modifier: Modifier = Modifier,
    text       : String,
    elevation  : androidx.compose.ui.unit.Dp = 0.dp,
    onDelete   : () -> Unit,
    // Removed unused parameters
) {
    Surface(
        color         = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape         = RoundedCornerShape(8.dp),
        border        = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier      = modifier
            .fillMaxWidth()
            .height(40.dp),
        shadowElevation= elevation
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp) // ✨ NEW: Add general starting padding here
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(1f)
            ) {
                // REMOVED: Fixed width Box/Spacer for the old Reorder Index

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