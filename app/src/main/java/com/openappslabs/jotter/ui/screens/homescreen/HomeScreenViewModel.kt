/*
 * Copyright (c) 2026 Open Apps Labs
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

package com.openappslabs.jotter.ui.screens.homescreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.repository.CategoryRepository
import com.openappslabs.jotter.data.repository.NotesRepository
import com.openappslabs.jotter.data.repository.UserPreferencesRepository
import com.openappslabs.jotter.data.model.SortDirection
import com.openappslabs.jotter.data.model.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.comparisons.compareByDescending
import kotlin.comparisons.then

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<UiState> = combine(
        notesRepository.getAllNotes(),
        categoryRepository.getAllCategories().map { list -> list.map { it.name } },
        userPreferencesRepository.userPreferencesFlow,
        _selectedCategory,
        _searchQuery
    ) { notes, categories, prefs, selectedCategory, searchQuery ->
        val activeCategories = categories.filter { category ->
            notes.any { it.category == category }
        }

        val validatedCategory = if (selectedCategory != "All" && 
            !listOf("Pinned", "Locked").contains(selectedCategory) && 
            !activeCategories.contains(selectedCategory)) {
            "All"
        } else {
            selectedCategory
        }

        val filteredNotes = if (searchQuery.isNotBlank()) {
            notes.filter {
                !it.isLocked && (
                    it.title.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true)
                )
            }
        } else {
            when (validatedCategory) {
                "All"     -> notes
                "Pinned"  -> notes.filter { it.isPinned }
                "Locked"  -> notes.filter { it.isLocked }
                else      -> notes.filter { it.category == validatedCategory }
            }
        }

        val sortType = SortType.valueOf(prefs.sortType)
        val sortDirection = SortDirection.valueOf(prefs.sortDirection)

        val baseComparator: Comparator<Note> = when (sortType) {
            SortType.ALPHABETICAL -> Comparator { a, b -> a.title.compareTo(b.title, ignoreCase = true) }
            SortType.CREATED -> Comparator { a, b -> a.createdTime.compareTo(b.createdTime) }
            SortType.LAST_UPDATED -> Comparator { a, b -> a.updatedTime.compareTo(b.updatedTime) }
        }
        val comparator = if (sortDirection == SortDirection.DESCENDING) baseComparator.reversed() else baseComparator

        val sortedNotes = filteredNotes.sortedWith(
            compareByDescending<Note> { it.isPinned }.then(comparator)
        )

        UiState(
            allNotes = sortedNotes,
            selectedCategory = validatedCategory,
            searchQuery = searchQuery,
            isGridView = prefs.isGridView,
            allAvailableCategories = activeCategories,
            showAddCategoryButton = prefs.showAddCategoryButton,
            showSortButton = prefs.showSortButton,
            isBiometricEnabled = prefs.isBiometricEnabled,
            dateFormat = prefs.dateFormat,
            sortType = sortType,
            sortDirection = sortDirection
        )
    }
    .distinctUntilChanged()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    @Immutable
    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val selectedCategory: String = "All",
        val searchQuery: String = "",
        val isGridView: Boolean = true,
        val allAvailableCategories: List<String> = emptyList(),
        val showAddCategoryButton: Boolean = true,
        val showSortButton: Boolean = false,
        val isBiometricEnabled: Boolean = false,
        val dateFormat: String = "dd MMM",
        val sortType: SortType = SortType.LAST_UPDATED,
        val sortDirection: SortDirection = SortDirection.DESCENDING
    )

    fun toggleGridView() {
        val currentIsGrid = uiState.value.isGridView
        viewModelScope.launch {
            userPreferencesRepository.setGridView(!currentIsGrid)
        }
    }

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            userPreferencesRepository.setSortType(sortType.name)
        }
    }

    fun setSortDirection(sortDirection: SortDirection) {
        viewModelScope.launch {
            userPreferencesRepository.setSortDirection(sortDirection.name)
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onNoteClicked(noteId: Int) {
    }
}