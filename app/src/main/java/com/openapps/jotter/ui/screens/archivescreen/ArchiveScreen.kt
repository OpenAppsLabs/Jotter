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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.components.Header
import com.openapps.jotter.ui.components.NoteCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArchiveScreen(
    onBackClick: () -> Unit
) {
    // In a real app, you would filter for { it.isArchived }
    val archivedNotes = sampleNotes.reversed()

    // Helper for Date Formatting
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
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
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(archivedNotes, key = { it.id }) { note ->

                        val dateStr = remember(note.updatedTime) {
                            dateFormatter.format(Date(note.updatedTime))
                        }

                        NoteCard(
                            title = note.title,
                            content = note.content,
                            // ✨ NEW PARAMS ADDED
                            date = dateStr,
                            category = note.category,
                            isPinned = note.isPinned,
                            isLocked = note.isLocked,
                            isGridView = true,
                            onClick = { /* TODO: Open Note */ }
                        )
                    }
                }
            }
        }
    }
}