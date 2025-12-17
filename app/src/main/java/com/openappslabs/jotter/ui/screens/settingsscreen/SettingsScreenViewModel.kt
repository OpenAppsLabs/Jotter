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

package com.openappslabs.jotter.ui.screens.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.data.repository.NotesRepository
import com.openappslabs.jotter.data.repository.UserPreferencesRepository
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
    val uiState: StateFlow<UiState> = repository.userPreferencesFlow
        .map { prefs ->
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
                isGridView = prefs.isGridView,
                is24HourFormat = prefs.is24HourFormat,
                dateFormat = prefs.dateFormat
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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
        val isGridView: Boolean = false,
        val is24HourFormat: Boolean = false,
        val dateFormat: String = "dd MMM"
    )

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
                notesRepository.unlockAllNotes()
            }
        }
    }

    fun updateSecureMode(isEnabled: Boolean) {
        viewModelScope.launch { repository.setSecureMode(isEnabled) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            notesRepository.clearAllDatabaseData()
            repository.clearAllData()
        }
    }

    fun updateGridView(isGrid: Boolean) {
        viewModelScope.launch { repository.setGridView(isGrid) }
    }

    fun updateTimeFormat(is24Hour: Boolean) {
        viewModelScope.launch { repository.setTimeFormat(is24Hour) }
    }

    fun updateDateFormat(format: String) {
        viewModelScope.launch { repository.setDateFormat(format) }
    }
}