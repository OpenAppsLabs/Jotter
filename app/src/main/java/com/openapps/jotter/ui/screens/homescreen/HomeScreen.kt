package com.openapps.jotter.ui.screens.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.components.CategoryBar
import com.openapps.jotter.ui.components.FAB
import com.openapps.jotter.ui.components.NoteCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Removed import: com.openapps.jotter.ui.components.Header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }

    // 1. Extract Categories
    val categories = remember {
        sampleNotes.map { it.category }.distinct().sorted()
    }

    // 2. Check for Status Notes
    val hasPinnedNotes = remember { sampleNotes.any { it.isPinned } }
    val hasLockedNotes = remember { sampleNotes.any { it.isLocked } }

    // 3. Filtering Logic
    val filteredNotes by remember(selectedCategory) {
        derivedStateOf {
            when (selectedCategory) {
                "All" -> sampleNotes
                "Pinned" -> sampleNotes.filter { it.isPinned }
                "Locked" -> sampleNotes.filter { it.isLocked }
                else -> sampleNotes.filter { it.category == selectedCategory }
            }
        }
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        // ✨ REFACTORED: Local TopAppBar definition
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jotter.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    // View Toggle Button
                    FilledTonalIconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView,
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Settings Button
                    FilledTonalIconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Optical balance padding
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FAB(onClick = onAddNoteClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategoryBar(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                onAddCategoryClick = onAddCategoryClick,
                hasPinnedNotes = hasPinnedNotes,
                hasLockedNotes = hasLockedNotes,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(if (isGridView) 2 else 1),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    val dateStr = remember(note.updatedTime) {
                        dateFormatter.format(Date(note.updatedTime))
                    }

                    NoteCard(
                        title = note.title,
                        content = note.content,
                        date = dateStr,
                        category = note.category,
                        isPinned = note.isPinned,
                        isLocked = note.isLocked,
                        isGridView = isGridView,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}