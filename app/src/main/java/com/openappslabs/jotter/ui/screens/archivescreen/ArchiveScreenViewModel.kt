package com.openappslabs.jotter.ui.screens.archivescreen

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Internal state for dialogs
    private val _showRestoreAllDialog = MutableStateFlow(false)
    private val _showBulkActionDialog = MutableStateFlow(false) // ✨ NEW STATE for Bulk Action Dialog

    // Combine Notes Flow from Room + User Prefs Flow + Dialog States
    val uiState: StateFlow<UiState> = combine(
        notesRepository.getArchivedNotes(),
        userPreferencesRepository.userPreferencesFlow,
        _showRestoreAllDialog,
        _showBulkActionDialog // ✨ Include new dialog state
    ) { notes, prefs, showRestore, showBulk ->
        UiState(
            archivedNotes = notes,
            isGridView = prefs.isGridView,
            showRestoreAllDialog = showRestore,
            showBulkActionDialog = showBulk // ✨ Expose new dialog state
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val archivedNotes: List<Note> = emptyList(),
        val isGridView: Boolean = true,
        val showRestoreAllDialog: Boolean = false,
        val showBulkActionDialog: Boolean = false // ✨ ADDED to UiState
    )

    // --- Restore All Actions ---

    fun onRestoreAllClicked() {
        _showRestoreAllDialog.value = true
    }

    fun confirmRestoreAll() {
        viewModelScope.launch {
            val archivedNotes = notesRepository.getArchivedNotes().first()
            archivedNotes.forEach { note ->
                notesRepository.unarchiveNote(note)
            }
            _showRestoreAllDialog.value = false
        }
    }

    fun dismissRestoreAllDialog() {
        _showRestoreAllDialog.value = false
    }

    // --- Bulk Action Menu Actions (Triggered by MoreVert/Actions button) ---

    fun onBulkActionClicked() { // ✨ NEW ACTION: Trigger the options dialog
        _showBulkActionDialog.value = true
    }

    fun dismissBulkActionDialog() { // ✨ NEW ACTION
        _showBulkActionDialog.value = false
    }

    fun confirmMoveAllToTrash() { // ✨ NEW ACTION: Moves all archived notes to trash
        viewModelScope.launch {
            val archivedNotes = notesRepository.getArchivedNotes().first()

            // Loop through all archived notes and move them to trash
            archivedNotes.forEach { note ->
                notesRepository.trashNote(note) // Uses existing status update logic
            }
            _showBulkActionDialog.value = false
        }
    }

    fun onNoteClicked(noteId: Int) {
        // handle note click (e.g., navigation)
    }
}