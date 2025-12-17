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

package com.openappslabs.jotter.ui.screens.backuprestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.openappslabs.jotter.data.model.BackupData
import com.openappslabs.jotter.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

data class BackupRestoreUiState(
    val isExportInProgress: Boolean = false,
    val isImportInProgress: Boolean = false,
    val lastExportSuccess: Boolean? = null,
    val lastImportSuccess: Boolean? = null,
    val errorMessage: String? = null,
    val hasDataToExport: Boolean = false
)

@HiltViewModel
class BackupRestoreScreenViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {
    private val _internalUiState = MutableStateFlow(BackupRestoreUiState())
    val uiState: StateFlow<BackupRestoreUiState> = combine(
        _internalUiState,
        repository.getAllNotes(),
        repository.getArchivedNotes(),
        repository.getCategories()
    ) { currentUi, notes, archived, categories ->
        val hasData = notes.isNotEmpty() || archived.isNotEmpty() || categories.isNotEmpty()
        currentUi.copy(hasDataToExport = hasData)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackupRestoreUiState()
    )

    private val gson = Gson()

    fun exportNotes(onReady: (String) -> Unit, onEmpty: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _internalUiState.update { it.copy(isExportInProgress = true, errorMessage = null, lastExportSuccess = null) }

            try {
                val (notes, categories) = repository.getBackupData()
                if (notes.isEmpty() && categories.isEmpty()) {
                    _internalUiState.update { it.copy(isExportInProgress = false) }
                    withContext(Dispatchers.Main) {
                        onEmpty()
                    }
                    return@launch
                }

                val backupData = BackupData(notes, categories)
                val jsonString = gson.toJson(backupData)

                _internalUiState.update { it.copy(isExportInProgress = false, lastExportSuccess = true) }

                withContext(Dispatchers.Main) {
                    onReady(jsonString)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _internalUiState.update {
                    it.copy(isExportInProgress = false, lastExportSuccess = false, errorMessage = e.localizedMessage)
                }
            }
        }
    }
    fun importNotes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            _internalUiState.update { it.copy(isImportInProgress = true, errorMessage = null, lastImportSuccess = null) }

            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                val backupData = gson.fromJson(jsonString, BackupData::class.java)
                val safeNotes = backupData.notes ?: emptyList()
                val safeCategories = backupData.categories ?: emptyList()

                repository.restoreBackupData(safeNotes, safeCategories)
                _internalUiState.update { it.copy(isImportInProgress = false, lastImportSuccess = true) }

            } catch (e: Exception) {
                e.printStackTrace()
                _internalUiState.update {
                    it.copy(isImportInProgress = false, lastImportSuccess = false, errorMessage = "Invalid Backup File")
                }
            }
        }
    }

    fun clearError() {
        _internalUiState.update { it.copy(errorMessage = null, lastExportSuccess = null, lastImportSuccess = null) }
    }
}