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

package com.openappslabs.jotter.ui.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.repository.NotesRepository
import com.openappslabs.jotter.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _categoryFlow = MutableStateFlow("All")
    val uiState: StateFlow<UiState> = combine(
        notesRepository.getAllNotes(),
        userPreferencesRepository.userPreferencesFlow,
        _categoryFlow
    ) { notes, prefs, selectedCategory ->

        val allAvailableCategories = notes
            .map { it.category }
            .distinct()
            .filter { it.isNotBlank() }
            .sorted()

        val validatedCategory = if (selectedCategory != "All" && !allAvailableCategories.contains(selectedCategory)) {
            "All"
        } else {
            selectedCategory
        }

        if (validatedCategory != selectedCategory) {
            viewModelScope.launch {
                _categoryFlow.value = validatedCategory
            }
        }

        val filteredNotes = when (validatedCategory) {
            "All"     -> notes
            "Pinned"  -> notes.filter { it.isPinned }
            "Locked"  -> notes.filter { it.isLocked }
            else      -> notes.filter { it.category == validatedCategory }
        }

        UiState(
            allNotes = filteredNotes,
            selectedCategory = validatedCategory,
            isGridView = prefs.isGridView,
            allAvailableCategories = allAvailableCategories,
            showAddCategoryButton = prefs.showAddCategoryButton,
            isBiometricEnabled = prefs.isBiometricEnabled,
            dateFormat = prefs.dateFormat
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val selectedCategory: String = "All",
        val isGridView: Boolean = true,
        val allAvailableCategories: List<String> = emptyList(),
        val showAddCategoryButton: Boolean = true,
        val isBiometricEnabled: Boolean = false,
        val dateFormat: String = "dd MMM"
    )

    fun toggleGridView() {
        val currentIsGrid = uiState.value.isGridView
        viewModelScope.launch {
            userPreferencesRepository.setGridView(!currentIsGrid)
        }
    }

    fun selectCategory(category: String) {
        _categoryFlow.value = category
    }

    fun onNoteClicked(noteId: Int) { }
    fun onAddNoteClick() { }
    fun onAddCategoryClick() { }
    fun onSettingsClick() { }
}