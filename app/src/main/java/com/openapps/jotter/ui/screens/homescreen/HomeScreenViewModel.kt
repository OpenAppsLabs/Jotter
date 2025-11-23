package com.openapps.jotter.ui.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.Note
import com.openapps.jotter.data.repository.UserPreferencesRepository
import com.openapps.jotter.data.sampleNotes
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
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Internal flows for data that isn't in DataStore yet (Notes & Category)
    private val _notesFlow = MutableStateFlow(sampleNotes)
    private val _categoryFlow = MutableStateFlow("All")

    // 1. Reactive UI State
    // Combines DataStore prefs + Local Notes + Local Category Selection
    val uiState: StateFlow<UiState> = combine(
        repository.userPreferencesFlow,
        _notesFlow,
        _categoryFlow
    ) { prefs, notes, category ->
        UiState(
            allNotes = notes,
            selectedCategory = category,
            isGridView = prefs.isGridView // <--- Controlled by DataStore
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val selectedCategory: String = "All",
        val isGridView: Boolean = true
    )

    // 2. Actions

    fun toggleGridView() {
        // We read the current value from the reactive state and flip it
        val currentIsGrid = uiState.value.isGridView
        viewModelScope.launch {
            repository.setGridView(!currentIsGrid)
        }
    }

    fun selectCategory(category: String) {
        _categoryFlow.value = category
    }

    fun onNoteClicked(noteId: Int) {
        // Navigation event handled by UI callback
    }

    fun onAddNoteClick() {
        // Navigation event handled by UI callback
    }

    fun onAddCategoryClick() {
        // Navigation event handled by UI callback
    }

    fun onSettingsClick() {
        // Navigation event handled by UI callback
    }
}