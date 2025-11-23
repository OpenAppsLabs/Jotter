package com.openapps.jotter.ui.screens.notedetailscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.sampleNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
    // In future: private val notesRepository: NotesRepository
) : ViewModel() {

    // 1. Get Note ID from Navigation Arguments
    private val noteId: Int? = savedStateHandle.get<Int>("noteId")

    // 2. UI State Definition
    data class UiState(
        val id: Int? = null,
        val title: String = "",
        val content: String = "",
        val category: String = "Uncategorized",
        val isPinned: Boolean = false,
        val isLocked: Boolean = false,
        val isArchived: Boolean = false,
        val isTrashed: Boolean = false,
        val lastEdited: Long = System.currentTimeMillis(),
        val isNotePersisted: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        if (noteId != null && noteId != -1) {
            loadNote(noteId)
        } else {
            // New Note State
            _uiState.update { it.copy(isNotePersisted = false) }
        }
    }

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            // Simulate fetching from DB using sampleNotes
            val note = sampleNotes.find { it.id == id }
            if (note != null) {
                _uiState.update {
                    it.copy(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        category = note.category,
                        isPinned = note.isPinned,
                        isLocked = note.isLocked,
                        // Assuming sample data doesn't have archived/trashed yet, defaulting to false
                        isArchived = false,
                        isTrashed = false,
                        lastEdited = note.updatedTime,
                        isNotePersisted = true
                    )
                }
            }
        }
    }

    // --- User Actions ---

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateContent(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
        // In real app: immediate autosave or separate update call
    }

    fun toggleLock() {
        _uiState.update { it.copy(isLocked = !it.isLocked) }
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value
            // Logic to Insert or Update note in Room Database

            // On success:
            _uiState.update {
                it.copy(
                    isNotePersisted = true,
                    lastEdited = System.currentTimeMillis()
                )
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            // Logic to move to trash or delete permanently
        }
    }

    fun restoreNote() {
        viewModelScope.launch {
            // Logic to unarchive or untrash
            _uiState.update { it.copy(isArchived = false, isTrashed = false) }
        }
    }

    // Add this inside NoteDetailViewModel class
    fun undoChanges() {
        // If it's an existing note, reload the original data from source
        if (noteId != null && noteId != -1) {
            loadNote(noteId)
        }
    }
}