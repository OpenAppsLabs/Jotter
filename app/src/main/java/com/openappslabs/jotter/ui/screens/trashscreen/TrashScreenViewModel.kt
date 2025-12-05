package com.openappslabs.jotter.ui.screens.trashscreen

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
import kotlinx.coroutines.flow.first // <-- Import needed for the fix
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Internal state for dialogs
    private val _showEmptyTrashDialog = MutableStateFlow(false)
    private val _showRestoreAllDialog = MutableStateFlow(false)

    // Combine Notes Flow from Room + User Prefs Flow + Dialog States
    val uiState: StateFlow<UiState> = combine(
        notesRepository.getTrashedNotes(),
        userPreferencesRepository.userPreferencesFlow,
        _showEmptyTrashDialog,
        _showRestoreAllDialog
    ) { notes, prefs, showEmpty, showRestore ->
        UiState(
            trashedNotes = notes,
            isGridView = prefs.isGridView,
            showEmptyTrashDialog = showEmpty,
            showRestoreAllDialog = showRestore
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val trashedNotes: List<Note> = emptyList(),
        val isGridView: Boolean = true,
        val showEmptyTrashDialog: Boolean = false,
        val showRestoreAllDialog: Boolean = false
    )

    // --- Empty Trash Actions ---

    fun onEmptyTrashClicked() {
        _showEmptyTrashDialog.value = true
    }

    fun confirmEmptyTrash() {
        viewModelScope.launch {
            notesRepository.emptyTrash()
            _showEmptyTrashDialog.value = false
        }
    }

    fun dismissEmptyTrashDialog() {
        _showEmptyTrashDialog.value = false
    }

    // --- Restore All Actions ---

    fun onRestoreAllClicked() {
        _showRestoreAllDialog.value = true
    }

    fun confirmRestoreAll() {
        viewModelScope.launch {
            // ✨ FIX: Use .first() to synchronously get the current List<Note> snapshot from the Flow
            val trashedNotes = notesRepository.getTrashedNotes().first()

            // Loop through all trashed notes and restore them
            trashedNotes.forEach { note ->
                notesRepository.restoreNote(note) // This is now correctly inside the launch coroutine
            }
            _showRestoreAllDialog.value = false
        }
    }

    fun dismissRestoreAllDialog() {
        _showRestoreAllDialog.value = false
    }

    fun onNoteClicked(noteId: Int) {
        // handle note click (e.g., navigation)
    }
}