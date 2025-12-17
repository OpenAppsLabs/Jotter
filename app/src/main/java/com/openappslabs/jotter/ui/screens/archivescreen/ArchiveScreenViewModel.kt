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
    private val _showRestoreAllDialog = MutableStateFlow(false)
    private val _showBulkActionDialog = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        notesRepository.getArchivedNotes(),
        userPreferencesRepository.userPreferencesFlow,
        _showRestoreAllDialog,
        _showBulkActionDialog
    ) { notes, prefs, showRestore, showBulk ->
        UiState(
            archivedNotes = notes,
            isGridView = prefs.isGridView,
            showRestoreAllDialog = showRestore,
            showBulkActionDialog = showBulk
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
        val showBulkActionDialog: Boolean = false
    )

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

    fun onBulkActionClicked() {
        _showBulkActionDialog.value = true
    }

    fun dismissBulkActionDialog() {
        _showBulkActionDialog.value = false
    }

    fun confirmMoveAllToTrash() {
        viewModelScope.launch {
            val archivedNotes = notesRepository.getArchivedNotes().first()
            archivedNotes.forEach { note ->
                notesRepository.trashNote(note)
            }
            _showBulkActionDialog.value = false
        }
    }

    fun onNoteClicked(noteId: Int) {
    }
}