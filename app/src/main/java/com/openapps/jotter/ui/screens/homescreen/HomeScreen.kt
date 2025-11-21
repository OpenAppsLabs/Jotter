package com.openapps.jotter.ui.screens.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.components.CategoryBar
import com.openapps.jotter.ui.components.FAB
import com.openapps.jotter.ui.components.Header
import com.openapps.jotter.ui.components.NoteCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // ✨ 2. Check for Pinned Notes
    val hasPinnedNotes = remember {
        sampleNotes.any { it.isPinned }
    }

    // ✨ 3. Updated Filtering Logic to handle "Pinned" selection
    val filteredNotes by remember(selectedCategory) {
        derivedStateOf {
            when (selectedCategory) {
                "All" -> sampleNotes
                "Pinned" -> sampleNotes.filter { it.isPinned } // Filter for pinned notes
                else -> sampleNotes.filter { it.category == selectedCategory }
            }
        }
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            Header(
                title = "Jotter.",
                isGridView = isGridView,
                onToggleView = { isGridView = !isGridView },
                onSettingsClick = onSettingsClick
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
                // ✨ 4. Pass the boolean to show the Pinned chip
                hasPinnedNotes = hasPinnedNotes,
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