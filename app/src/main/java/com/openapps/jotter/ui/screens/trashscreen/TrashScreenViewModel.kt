package com.openapps.jotter.ui.screens.trashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.Note
import com.openapps.jotter.data.sampleNotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrashScreenViewModel : ViewModel() {

    data class UiState(
        val trashedNotes: List<Note> = emptyList(),
        val showEmptyTrashDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadMockTrashedNotes()
    }

    private fun loadMockTrashedNotes() {
        viewModelScope.launch {
            val notes = sampleNotes.filter { it.isTrashed && !it.isArchived }
            _uiState.update { current ->
                current.copy(trashedNotes = notes)
            }
        }
    }

    fun onEmptyTrashClicked() {
        _uiState.update { current ->
            current.copy(showEmptyTrashDialog = true)
        }
    }

    fun confirmEmptyTrash() {
        viewModelScope.launch {
            // For mock data scenario: just clear the list
            _uiState.update {
                UiState(
                    trashedNotes = emptyList(),
                    showEmptyTrashDialog = false
                )
            }
        }
    }

    fun dismissEmptyTrashDialog() {
        _uiState.update { current ->
            current.copy(showEmptyTrashDialog = false)
        }
    }

    fun onNoteClicked(noteId: Int) {
        // You can handle navigation event here if needed
    }
}
