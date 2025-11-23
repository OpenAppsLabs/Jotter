package com.openapps.jotter.ui.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.Note
import com.openapps.jotter.data.sampleNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    // In future: private val notesRepository: NotesRepository
) : ViewModel() {

    data class UiState(
        val allNotes: List<Note> = emptyList(),
        val selectedCategory: String = "All",
        val isGridView: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadMockNotes()
    }

    private fun loadMockNotes() {
        viewModelScope.launch {
            // load mock data
            _uiState.update {
                it.copy(allNotes = sampleNotes)
            }
        }
    }

    fun toggleGridView() {
        _uiState.update {
            it.copy(isGridView = !it.isGridView)
        }
    }

    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    fun onNoteClicked(noteId: Int) {
        // Navigation event or further logic
    }

    fun onAddNoteClick() {
        // Navigation event or logic
    }

    fun onAddCategoryClick() {
        // Navigation event or logic
    }

    fun onSettingsClick() {
        // Navigation event or logic
    }
}
