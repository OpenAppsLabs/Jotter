package com.openapps.jotter.ui.screens.archivescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openapps.jotter.data.sampleNotes
import com.openapps.jotter.ui.components.NoteCard
import com.openapps.jotter.ui.components.RestoreAllDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    onBackClick: () -> Unit,
    onNoteClick: (Int) -> Unit = {}
) {
    var showRestoreAllDialog by remember { mutableStateOf(false) }

    val archivedNotes = remember {
        sampleNotes.filter { it.isArchived && !it.isTrashed }
    }

    val onRestoreAllClick: () -> Unit = {
        showRestoreAllDialog = true
    }
    val canRestoreAll = archivedNotes.isNotEmpty()
    val noteCount = archivedNotes.size

    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Archive",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    // Back Arrow (Standard Circular Surface)
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(start = 12.dp).size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                actions = {
                    // ✨ REFACTORED: Restore All Button (Using Circular Surface)
                    if (canRestoreAll) {
                        Surface(
                            onClick = onRestoreAllClick,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            enabled = canRestoreAll,
                            modifier = Modifier.padding(end = 12.dp).size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Restore All",
                                    tint = MaterialTheme.colorScheme.primary, // Ensure primary tint
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                    EmptyArchiveContent()
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
                            date = dateStr,
                            category = note.category,
                            isPinned = note.isPinned,
                            isLocked = note.isLocked,
                            isGridView = true,
                            onClick = { onNoteClick(note.id) }
                        )
                    }
                }
            }
        }

        if (showRestoreAllDialog) {
            RestoreAllDialog(
                noteCount = noteCount,
                onDismiss = { showRestoreAllDialog = false },
                onConfirm = {
                    // TODO: Implement actual bulk restore logic here
                    showRestoreAllDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyArchiveContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = "Archive Icon",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Archive is Empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Notes you archive will appear here until they are restored or permanently deleted.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}