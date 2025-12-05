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

    // Internal flow for category selection (remains local)
    private val _categoryFlow = MutableStateFlow("All")

    // 1. Reactive UI State
    val uiState: StateFlow<UiState> = combine(
        notesRepository.getAllNotes(), // Live notes from Room (All notes, unfiltered by selectedCategory)
        userPreferencesRepository.userPreferencesFlow,
        _categoryFlow // Last selected chip state
    ) { notes, prefs, selectedCategory ->

        // --- Calculation of ALL Available Categories (Existing) ---
        // Get the list of all unique categories from ALL notes (excluding empty strings)
        val allAvailableCategories = notes
            .map { it.category }
            .distinct()
            .filter { it.isNotBlank() } // Exclude the internal 'no category' marker
            .sorted()

        // --- Validation Logic (Existing) ---
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

        // --- Filtering Logic (Existing) ---
        val filteredNotes = when (validatedCategory) {
            "All"     -> notes
            "Pinned"  -> notes.filter { it.isPinned }
            "Locked"  -> notes.filter { it.isLocked }
            else      -> notes.filter { it.category == validatedCategory }
        }

        UiState(
            allNotes = filteredNotes, // Filtered list goes to the grid
            selectedCategory = validatedCategory, // Validated state
            isGridView = prefs.isGridView,
            allAvailableCategories = allAvailableCategories,
            showAddCategoryButton = prefs.showAddCategoryButton, // ✨ ADDED: Map preference from repository
            isBiometricEnabled = prefs.isBiometricEnabled // ✨ ADDED: Propagate lock preference
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
        val showAddCategoryButton: Boolean = true, // ✨ ADDED: New property for UI control
        val isBiometricEnabled: Boolean = false // ✨ ADDED: Check if lock is enabled globally
    )

    // 2. Actions

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