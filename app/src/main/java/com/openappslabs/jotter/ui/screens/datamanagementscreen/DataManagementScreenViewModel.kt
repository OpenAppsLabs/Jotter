/*
 * Copyright (c) 2026 Open Apps Labs
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

package com.openappslabs.jotter.ui.screens.datamanagementscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.data.model.BackupData
import com.openappslabs.jotter.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject

@Immutable
data class DataManagementUiState(
    val isExportInProgress: Boolean = false,
    val isImportInProgress: Boolean = false,
    val lastExportSuccess: Boolean? = null,
    val lastImportSuccess: Boolean? = null,
    val errorMessage: String? = null,
    val hasDataToExport: Boolean = false
)

@HiltViewModel
class DataManagementScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    private val _internalUiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = combine(
        _internalUiState,
        notesRepository.getAllNotes(),
        notesRepository.getCategories()
    ) { currentUi, notes, categories ->
        val hasData = notes.isNotEmpty() || categories.isNotEmpty()
        currentUi.copy(hasDataToExport = hasData)
    }
    .distinctUntilChanged()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DataManagementUiState()
    )

    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }

    fun exportNotes(onReady: (String) -> Unit, onEmpty: () -> Unit) {
        viewModelScope.launch {
            _internalUiState.update { it.copy(isExportInProgress = true, errorMessage = null, lastExportSuccess = null) }

            try {
                val jsonString = withContext(Dispatchers.IO) {
                    val backupData = notesRepository.getBackupData()
                    if (backupData.notes.isEmpty() && backupData.categories.isEmpty()) {
                        null
                    } else {
                        json.encodeToString(backupData)
                    }
                }

                if (jsonString == null) {
                    _internalUiState.update { it.copy(isExportInProgress = false) }
                    onEmpty()
                } else {
                    _internalUiState.update { it.copy(isExportInProgress = false, lastExportSuccess = true) }
                    onReady(jsonString)
                }

            } catch (e: Exception) {
                _internalUiState.update {
                    it.copy(isExportInProgress = false, lastExportSuccess = false, errorMessage = e.localizedMessage)
                }
            }
        }
    }

    fun importNotes(inputStream: InputStream) {
        viewModelScope.launch {
            _internalUiState.update { it.copy(isImportInProgress = true, errorMessage = null, lastImportSuccess = null) }

            try {
                withContext(Dispatchers.IO) {
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val backupData = json.decodeFromString<BackupData>(jsonString)
                    notesRepository.restoreBackupData(backupData)
                }
                
                _internalUiState.update { it.copy(isImportInProgress = false, lastImportSuccess = true) }

            } catch (e: Exception) {
                _internalUiState.update {
                    it.copy(isImportInProgress = false, lastImportSuccess = false, errorMessage = "Invalid or Corrupted Backup File")
                }
            }
        }
    }

    fun clearError() {
        _internalUiState.update { it.copy(errorMessage = null, lastExportSuccess = null, lastImportSuccess = null) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            notesRepository.clearAllDatabaseData()
        }
    }
}