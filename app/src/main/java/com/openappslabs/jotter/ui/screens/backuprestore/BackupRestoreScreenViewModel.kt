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

/**
 * UI state for Backup & Restore screen.
 */
data class BackupRestoreUiState(
    val isExportInProgress: Boolean = false,
    val isImportInProgress: Boolean = false,
    val lastExportSuccess: Boolean? = null,
    val lastImportSuccess: Boolean? = null,
    val errorMessage: String? = null,
    val hasDataToExport: Boolean = false // ✨ NEW FIELD: Tracks if db has data
)

@HiltViewModel
class BackupRestoreScreenViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    // Internal state for loading/error flags
    private val _internalUiState = MutableStateFlow(BackupRestoreUiState())

    // ✨ Combine internal state with database flows to know if data exists in real-time
    val uiState: StateFlow<BackupRestoreUiState> = combine(
        _internalUiState,
        repository.getAllNotes(),      // Active notes
        repository.getArchivedNotes(), // Archived notes
        repository.getCategories()     // Categories
    ) { currentUi, notes, archived, categories ->

        // Check if there is ANY data to export
        val hasData = notes.isNotEmpty() || archived.isNotEmpty() || categories.isNotEmpty()

        currentUi.copy(hasDataToExport = hasData)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackupRestoreUiState()
    )

    private val gson = Gson()

    /**
     * Generates the Backup JSON string.
     * @param onReady Callback with the JSON string when data is ready to save.
     * @param onEmpty Callback fired if there is no data to export.
     */
    fun exportNotes(onReady: (String) -> Unit, onEmpty: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Start Loading
            _internalUiState.update { it.copy(isExportInProgress = true, errorMessage = null, lastExportSuccess = null) }

            try {
                // 2. Fetch Data Snapshot
                val (notes, categories) = repository.getBackupData()

                // ✨ FIX: Check if data exists before generating JSON
                if (notes.isEmpty() && categories.isEmpty()) {
                    _internalUiState.update { it.copy(isExportInProgress = false) }
                    withContext(Dispatchers.Main) {
                        onEmpty() // Notify UI to show "No Data" dialog
                    }
                    return@launch
                }

                val backupData = BackupData(notes, categories)

                // 3. Convert to JSON
                val jsonString = gson.toJson(backupData)

                // 4. Update State & Callback
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

    /**
     * Reads the file stream, parses JSON, and restores to Database.
     */
    fun importNotes(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Start Loading
            _internalUiState.update { it.copy(isImportInProgress = true, errorMessage = null, lastImportSuccess = null) }

            try {
                // 2. Read & Parse
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                val backupData = gson.fromJson(jsonString, BackupData::class.java)

                // ✨ FIX: Null safety checks for imported data
                // Gson might return null for missing fields even if Kotlin type is non-nullable
                val safeNotes = backupData.notes ?: emptyList()
                val safeCategories = backupData.categories ?: emptyList()

                // 3. Restore to DB
                repository.restoreBackupData(safeNotes, safeCategories)

                // 4. Success
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