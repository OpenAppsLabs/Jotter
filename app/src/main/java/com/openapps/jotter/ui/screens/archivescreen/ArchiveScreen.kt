package com.openapps.jotter.ui.screens.archivescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.components.Header
import com.openapps.jotter.ui.components.NoteCard

@Composable
fun ArchiveScreen(
    onBackClick: () -> Unit
) {
    // In a real app, you would filter for { it.isArchived }
    // For now, we just reverse the sample list to make it look different from Home
    val archivedNotes = sampleNotes.reversed()

    Scaffold(
        topBar = {
            // Uses the "Detail Mode" of your Header (Centered + Back Button)
            Header(
                title = "Archive",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (archivedNotes.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No archived notes",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Grid Content
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(archivedNotes, key = { it.id }) { note ->
                        NoteCard(
                            title = note.title,
                            content = note.content,
                            isGridView = true, // Force grid view for Archive
                            onClick = { /* TODO: Open Note */ }
                        )
                    }
                }
            }
        }
    }
}