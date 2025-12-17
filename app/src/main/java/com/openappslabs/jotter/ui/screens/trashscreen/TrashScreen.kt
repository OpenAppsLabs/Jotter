/*
 * Copyright (c) 2025 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.openappslabs.jotter.ui.screens.trashscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openappslabs.jotter.ui.components.EmptyTrashDialog
import com.openappslabs.jotter.ui.components.NoteCard
import com.openappslabs.jotter.ui.components.RestoreAllDialog // Assuming this component exists
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Box as ComposeBox
import androidx.compose.foundation.layout.Column as ComposeColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    onBackClick: () -> Unit,
    onNoteClick: (Int) -> Unit = {},
    viewModel: TrashScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val trashedNotes = uiState.trashedNotes
    val showEmptyTrashDialog = uiState.showEmptyTrashDialog
    val showRestoreDialog = uiState.showRestoreAllDialog
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Trash",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(start = 12.dp).size(48.dp)
                    ) {
                        ComposeBox(contentAlignment = Alignment.Center) {
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
                    if (trashedNotes.isNotEmpty()) {
                        Surface(
                            onClick = { viewModel.onRestoreAllClicked() },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            enabled = true,
                            modifier = Modifier.padding(end = 12.dp).size(48.dp)
                        ) {
                            ComposeBox(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Restore All",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = Color.Unspecified
                )
            )
        },
        floatingActionButton = {
            if (trashedNotes.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onEmptyTrashClicked() },
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
                ComposeBox(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTrashContent()
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(if (uiState.isGridView) 2 else 1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(trashedNotes, key = { it.id }) { note ->
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
                            isGridView = uiState.isGridView,
                            onClick = {
                                viewModel.onNoteClicked(note.id)
                                onNoteClick(note.id)
                            }
                        )
                    }
                }
            }
        }

        if (showEmptyTrashDialog) {
            EmptyTrashDialog(
                onDismiss = { viewModel.dismissEmptyTrashDialog() },
                onConfirm = { viewModel.confirmEmptyTrash() }
            )
        }

        if (showRestoreDialog) {
            RestoreAllDialog(
                noteCount = trashedNotes.size,
                onDismiss = { viewModel.dismissRestoreAllDialog() },
                onConfirm = { viewModel.confirmRestoreAll() }
            )
        }
    }
}

@Composable
private fun EmptyTrashContent() {
    ComposeColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DeleteForever,
            contentDescription = "Trash Icon",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nothing in the Trash",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Notes you delete will appear here, and are automatically removed after 7 days.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}