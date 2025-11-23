package com.openapps.jotter.ui.screens.archivescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.Note
import com.openapps.jotter.data.sampleNotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArchiveScreenViewModel : ViewModel() {

    data class UiState(
        val archivedNotes: List<Note> = emptyList(),
        val showRestoreAllDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadMockArchivedNotes()
    }

    private fun loadMockArchivedNotes() {
        viewModelScope.launch {
            val notes = sampleNotes.filter { it.isArchived && !it.isTrashed }
            _uiState.update { it.copy(archivedNotes = notes) }
        }
    }

    fun onRestoreAllClicked() {
        _uiState.update { it.copy(showRestoreAllDialog = true) }
    }

    fun confirmRestoreAll() {
        viewModelScope.launch {
            // In mock mode: clear archived list (or you might want to reset isArchived flags)
            _uiState.update {
                UiState(
                    archivedNotes = emptyList(),
                    showRestoreAllDialog = false
                )
            }
        }
    }

    fun dismissRestoreAllDialog() {
        _uiState.update { it.copy(showRestoreAllDialog = false) }
    }

    fun onNoteClicked(noteId: Int) {
        // handle note click (e.g., navigation) if needed
    }
}
