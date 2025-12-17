/*
 * Copyright (c) 2025 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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
    private val _showEmptyTrashDialog = MutableStateFlow(false)
    private val _showRestoreAllDialog = MutableStateFlow(false)
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

    fun onRestoreAllClicked() {
        _showRestoreAllDialog.value = true
    }

    fun confirmRestoreAll() {
        viewModelScope.launch {
            val trashedNotes = notesRepository.getTrashedNotes().first()
            trashedNotes.forEach { note ->
                notesRepository.restoreNote(note)
            }
            _showRestoreAllDialog.value = false
        }
    }

    fun dismissRestoreAllDialog() {
        _showRestoreAllDialog.value = false
    }

    fun onNoteClicked(noteId: Int) {
    }
}