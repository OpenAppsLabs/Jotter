package com.openapps.jotter.ui.screens.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.repository.NotesRepository
import com.openapps.jotter.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repository: UserPreferencesRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    // 1. Convert Repository Flow -> UI State
    val uiState: StateFlow<UiState> = repository.userPreferencesFlow
        .map { prefs ->
            // When we receive data from the repository, loading is done (isLoading = false)
            UiState(
                isLoading = false,
                isDarkMode = prefs.isDarkMode,
                isTrueBlackEnabled = prefs.isTrueBlackEnabled,
                isDynamicColor = prefs.isDynamicColor,
                defaultOpenInEdit = prefs.defaultOpenInEdit,
                isHapticEnabled = prefs.isHapticEnabled,
                isBiometricEnabled = prefs.isBiometricEnabled,
                isSecureMode = prefs.isSecureMode,
                showAddCategoryButton = prefs.showAddCategoryButton,
                isGridView = prefs.isGridView
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // Start with isLoading = true to prevent UI flicker
            initialValue = UiState(isLoading = true)
        )

    data class UiState(
        val isLoading: Boolean = true,
        val isDarkMode: Boolean = false,
        val isTrueBlackEnabled: Boolean = false,
        val isDynamicColor: Boolean = true,
        val defaultOpenInEdit: Boolean = false,
        val isHapticEnabled: Boolean = true,
        val isBiometricEnabled: Boolean = false,
        val isSecureMode: Boolean = false,
        val showAddCategoryButton: Boolean = true,
        val isGridView: Boolean = false
    )

    // 2. User Actions -> Call Repository

    fun updateShowAddCategoryButton(show: Boolean) {
        viewModelScope.launch { repository.setShowAddCategoryButton(show) }
    }

    fun updateDarkMode(isEnabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(isEnabled) }
    }

    fun updateTrueBlackMode(isEnabled: Boolean) {
        viewModelScope.launch { repository.setTrueBlack(isEnabled) }
    }

    fun updateDynamicColor(isEnabled: Boolean) {
        viewModelScope.launch { repository.setDynamicColor(isEnabled) }
    }

    fun updateDefaultOpenInEdit(isEnabled: Boolean) {
        viewModelScope.launch { repository.setDefaultOpenInEdit(isEnabled) }
    }

    fun updateHapticEnabled(isEnabled: Boolean) {
        viewModelScope.launch { repository.setHaptic(isEnabled) }
    }

    fun updateBiometricEnabled(isEnabled: Boolean) {
        viewModelScope.launch { 
            repository.setBiometric(isEnabled) 
            if (!isEnabled) {
                notesRepository.unlockAllNotes() // ✨ Remove lock from all notes when disabling
            }
        }
    }

    fun updateSecureMode(isEnabled: Boolean) {
        viewModelScope.launch { repository.setSecureMode(isEnabled) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // 1. Wipe the Database (Notes & Categories)
            notesRepository.clearAllDatabaseData()

            // 2. Reset Preferences (keeping the Add Button toggle)
            repository.clearAllData()
        }
    }

    fun updateGridView(isGrid: Boolean) {
        viewModelScope.launch { repository.setGridView(isGrid) }
    }
}