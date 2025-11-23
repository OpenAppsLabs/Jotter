package com.openapps.jotter.ui.screens.trashscreen

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
class TrashScreenViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Internal flow for trashed notes (Still mocking for now)
    private val _trashedNotesFlow = MutableStateFlow(emptyList<Note>())

    // Internal state for dialogs (UI-specific ephemeral state)
    private val _showEmptyTrashDialog = MutableStateFlow(false)

    // Combine Repository preferences (for isGridView) with notes data and dialog state
    val uiState: StateFlow<UiState> = combine(
        repository.userPreferencesFlow,
        _trashedNotesFlow,
        _showEmptyTrashDialog
    ) { prefs, notes, showDialog ->
        UiState(
            trashedNotes = notes,
            isGridView = prefs.isGridView, // ✨ Now observing global state
            showEmptyTrashDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    data class UiState(
        val trashedNotes: List<Note> = emptyList(),
        val isGridView: Boolean = true, // ✨ Added isGridView state
        val showEmptyTrashDialog: Boolean = false
    )

    init {
        loadMockTrashedNotes()
    }

    private fun loadMockTrashedNotes() {
        viewModelScope.launch {
            // Load mock data (Filtered here for simplicity)
            val notes = sampleNotes.filter { it.isTrashed && !it.isArchived }
            _trashedNotesFlow.value = notes
        }
    }

    fun onEmptyTrashClicked() {
        _showEmptyTrashDialog.value = true
    }

    fun confirmEmptyTrash() {
        viewModelScope.launch {
            // For mock data scenario: clear the list
            _trashedNotesFlow.value = emptyList()
            _showEmptyTrashDialog.value = false

            // Future Note: Call repository.emptyTrash() here
        }
    }

    fun dismissEmptyTrashDialog() {
        _showEmptyTrashDialog.value = false
    }

    fun onNoteClicked(noteId: Int) {
        // handle note click if needed
    }
}