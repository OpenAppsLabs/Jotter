package com.openapps.jotter.ui.screens.trashscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openapps.jotter.ui.components.EmptyTrashDialog
import com.openapps.jotter.ui.components.Header
import com.openapps.jotter.ui.components.NoteCard
import com.openapps.jotter.data.sampleNotes

@Composable
fun TrashScreen(
    onBackClick: () -> Unit
) {
    // State to control dialog visibility
    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    // Mock Data
    val trashedNotes = sampleNotes.take(3)

    Scaffold(
        topBar = {
            Header(
                title = "Trash",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (trashedNotes.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    // CLICK: Open the dialog
                    onClick = { showEmptyTrashDialog = true },
                    icon = { Icon(Icons.Default.DeleteForever, contentDescription = null) },
                    text = { Text("Empty Trash") },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (trashedNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trash is empty",
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
                    items(trashedNotes, key = { it.id }) { note ->
                        NoteCard(
                            title = note.title,
                            content = note.content,
                            isGridView = true,
                            onClick = { /* TODO: Restore Dialog */ }
                        )
                    }
                }
            }
        }

        // LOGIC: Render the dialog if state is true
        if (showEmptyTrashDialog) {
            EmptyTrashDialog(
                onDismiss = { showEmptyTrashDialog = false },
                onConfirm = {
                    // TODO: Actual delete logic here
                    showEmptyTrashDialog = false
                }
            )
        }
    }
}