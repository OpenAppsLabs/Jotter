package com.openapps.jotter.ui.screens.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openapps.jotter.ui.components.CategoryBar
import com.openapps.jotter.ui.components.FAB
import com.openapps.jotter.ui.components.NoteCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyStaggeredGridState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    
    // Scroll to top smoothly whenever the selected category changes
    LaunchedEffect(uiState.selectedCategory) {
        listState.animateScrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text     = "Jotter",
                        style    = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Light
                    )
                },
                actions = {
//                    FilledTonalIconButton(onClick = { viewModel.toggleGridView() }) {
//                        Icon(
//                            imageVector = if (uiState.isGridView) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView,
//                            contentDescription = "Toggle View",
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalIconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector     = Icons.Outlined.Settings,
                            contentDescription= "Settings",
                            tint            = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = MaterialTheme.colorScheme.surface,
                    titleContentColor      = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        floatingActionButton = {
            FAB(
                onClick = onAddNoteClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategoryBar(
                // ✨ FIX: Pass the full, UNFILTERED list of categories from the ViewModel
                // Note: The ViewModel must expose a property called `allAvailableCategories`
                categories          = uiState.allAvailableCategories,
                selectedCategory    = uiState.selectedCategory,
                onCategorySelect    = { viewModel.selectCategory(it) },
                onAddCategoryClick  = onAddCategoryClick,
                showAddButton       = uiState.showAddCategoryButton,
                modifier            = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalStaggeredGrid(
                state                = listState,
                columns              = StaggeredGridCells.Fixed(if (uiState.isGridView) 2 else 1),
                modifier             = Modifier.fillMaxSize(),
                contentPadding       = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing  = 12.dp
            ) {
                items(uiState.allNotes, key = { it.id }) { note -> // Now using VM's filtered list directly
                    val dateStr = remember(note.updatedTime) {
                        dateFormatter.format(Date(note.updatedTime))
                    }
                    NoteCard(
                        title     = note.title,
                        content   = note.content,
                        date      = dateStr,
                        category  = note.category,
                        isPinned  = note.isPinned,
                        isLocked  = note.isLocked,
                        isGridView= uiState.isGridView,
                        onClick   = { viewModel.onNoteClicked(note.id); onNoteClick(note.id) },
                        modifier  = Modifier.animateItem()
                    )
                }
            }
        }
    }
}