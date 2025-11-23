package com.openapps.jotter.ui.screens.archivescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.Note
import com.openapps.jotter.data.repository.UserPreferencesRepository
import com.openapps.jotter.data.sampleNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveScreenViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Internal flow for archived notes (Still mocking for now)
    private val _archivedNotesFlow = MutableStateFlow(emptyList<Note>())

    // Internal state for dialogs (UI-specific ephemeral state)
    private val _showRestoreAllDialog = MutableStateFlow(false)

    // Combine Repository preferences (for isGridView) with the notes data and dialog state
    val uiState: StateFlow<UiState> = combine(
        repository.userPreferencesFlow,
        _archivedNotesFlow,
        _showRestoreAllDialog
    ) { prefs, notes, showDialog ->
        UiState(
            archivedNotes = notes,
            isGridView = prefs.isGridView, // ✨ Now observing global state
            showRestoreAllDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val archivedNotes: List<Note> = emptyList(),
        val isGridView: Boolean = true, // ✨ Added isGridView state
        val showRestoreAllDialog: Boolean = false
    )

    init {
        loadMockArchivedNotes()
    }

    private fun loadMockArchivedNotes() {
        viewModelScope.launch {
            // Load mock data (Filtered here for simplicity)
            val notes = sampleNotes.filter { it.isArchived && !it.isTrashed }
            _archivedNotesFlow.value = notes
        }
    }

    fun onRestoreAllClicked() {
        _showRestoreAllDialog.value = true
    }

    fun confirmRestoreAll() {
        viewModelScope.launch {
            // In mock mode: clear archived list
            _archivedNotesFlow.value = emptyList()
            _showRestoreAllDialog.value = false

            // Future Note: Call repository.restoreAllArchivedNotes() here
        }
    }

    fun dismissRestoreAllDialog() {
        _showRestoreAllDialog.value = false
    }

    fun onNoteClicked(noteId: Int) {
        // handle note click (e.g., navigation) if needed
    }
}