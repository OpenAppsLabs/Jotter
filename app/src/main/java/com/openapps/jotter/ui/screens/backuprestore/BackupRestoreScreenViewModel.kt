package com.openapps.jotter.ui.screens.backuprestore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI state for Backup & Restore screen.
 */
data class BackupRestoreUiState(
    val isExportInProgress: Boolean = false,
    val isImportInProgress: Boolean = false,
    val lastExportSuccess: Boolean? = null,
    val lastImportSuccess: Boolean? = null,
    val errorMessage: String? = null
)

class BackupRestoreScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow( BackupRestoreUiState() )
    val uiState: StateFlow<BackupRestoreUiState> = _uiState.asStateFlow()

    /** Called when user taps “Export Notes”. */
    fun exportNotes() {
        _uiState.value = _uiState.value.copy(
            isExportInProgress = true,
            errorMessage = null
        )
        // TODO: Trigger export logic (file save, encryption etc)
        // For mock mode: simulate success immediately
        _uiState.value = _uiState.value.copy(
            isExportInProgress = false,
            lastExportSuccess = true
        )
    }

    /** Called when user taps “Import Notes”. */
    fun importNotes() {
        _uiState.value = _uiState.value.copy(
            isImportInProgress = true,
            errorMessage = null
        )
        // TODO: Trigger import logic (file pick, parse, restore etc)
        // For mock mode: simulate success immediately
        _uiState.value = _uiState.value.copy(
            isImportInProgress = false,
            lastImportSuccess = true
        )
    }

    /** Reset any previous error state. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    /** Called when an error occurs during export/import. */
    fun onFailure(error: String) {
        _uiState.value = _uiState.value.copy(
            isExportInProgress = false,
            isImportInProgress = false,
            errorMessage = error
        )
    }
}
