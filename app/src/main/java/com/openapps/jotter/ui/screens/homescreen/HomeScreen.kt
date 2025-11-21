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

@Composable
fun HomeScreen(
    // 1. Add this parameter so we can navigate out
    onAddCategoryClick: () -> Unit,
    onSettingsClick: () -> Unit // Add this
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }

    val categories = remember {
        sampleNotes.map { it.category }.distinct().sorted()
    }

    val filteredNotes by remember(selectedCategory) {
        derivedStateOf {
            if (selectedCategory == "All") {
                sampleNotes
            } else {
                sampleNotes.filter { it.category == selectedCategory }
            }
        }
    }

    Scaffold(
        topBar = {
            Header(
                title = "Jotter",
                isGridView = isGridView,
                onToggleView = { isGridView = !isGridView },
                onSettingsClick = onSettingsClick // Connect it here
            )
        },
        floatingActionButton = {
            FAB(onClick = { /* TODO: Create Note */ })
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
                // 2. Connect the component's click to the navigation callback
                onAddCategoryClick = onAddCategoryClick,
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
                    NoteCard(
                        title = note.title,
                        content = note.content,
                        isGridView = isGridView,
                        onClick = { /* TODO: Open Note */ }
                    )
                }
            }
        }
    }
}