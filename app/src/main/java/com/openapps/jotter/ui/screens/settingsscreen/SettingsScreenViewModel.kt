package com.openapps.jotter.ui.screens.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    // In future: private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    data class UiState(
        val isDarkMode: Boolean = false,
        val isTrueBlackEnabled: Boolean = false,
        val isDynamicColor: Boolean = true,
        val defaultOpenInEdit: Boolean = false,
        val isHapticEnabled: Boolean = true,
        val isBiometricEnabled: Boolean = false,
        val isSecureMode: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading preferences from DataStore
            // For now, we rely on the default values in UiState
        }
    }

    fun updateDarkMode(isEnabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = isEnabled) }
        // save to preferences
    }

    fun updateTrueBlackMode(isEnabled: Boolean) {
        _uiState.update { it.copy(isTrueBlackEnabled = isEnabled) }
    }

    fun updateDynamicColor(isEnabled: Boolean) {
        _uiState.update { it.copy(isDynamicColor = isEnabled) }
    }

    fun updateDefaultOpenInEdit(isEnabled: Boolean) {
        _uiState.update { it.copy(defaultOpenInEdit = isEnabled) }
    }

    fun updateHapticEnabled(isEnabled: Boolean) {
        _uiState.update { it.copy(isHapticEnabled = isEnabled) }
    }

    fun updateBiometricEnabled(isEnabled: Boolean) {
        _uiState.update { it.copy(isBiometricEnabled = isEnabled) }
    }

    fun updateSecureMode(isEnabled: Boolean) {
        _uiState.update { it.copy(isSecureMode = isEnabled) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // Logic to wipe database and preferences
        }
    }
}