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

package com.openappslabs.jotter.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
data class UserPreferences(
    val isGridView: Boolean = false,
    val isDarkMode: Boolean = false,
    val isTrueBlackEnabled: Boolean = false,
    val isDynamicColor: Boolean = true,
    val defaultOpenInEdit: Boolean = false,
    val isHapticEnabled: Boolean = true,
    val isBiometricEnabled: Boolean = false,
    val isSecureMode: Boolean = false,
    val showAddCategoryButton: Boolean = true,
    val is24HourFormat: Boolean = false,
    val dateFormat: String = "dd MMM"
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val IS_GRID_VIEW = booleanPreferencesKey("is_grid_view")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val IS_TRUE_BLACK = booleanPreferencesKey("is_true_black")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey("is_dynamic_color")
        val DEFAULT_OPEN_EDIT = booleanPreferencesKey("default_open_edit")
        val IS_HAPTIC = booleanPreferencesKey("is_haptic")
        val IS_BIOMETRIC = booleanPreferencesKey("is_biometric")
        val IS_SECURE_MODE = booleanPreferencesKey("is_secure_mode")
        val SHOW_ADD_CATEGORY_BUTTON = booleanPreferencesKey("show_add_category_button")
        val IS_24_HOUR_FORMAT = booleanPreferencesKey("is_24_hour_format")
        val DATE_FORMAT = stringPreferencesKey("date_format")
    }
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                isGridView = preferences[Keys.IS_GRID_VIEW] ?: false,
                isDarkMode = preferences[Keys.IS_DARK_MODE] ?: false,
                isTrueBlackEnabled = preferences[Keys.IS_TRUE_BLACK] ?: false,
                isDynamicColor = preferences[Keys.IS_DYNAMIC_COLOR] ?: true,
                defaultOpenInEdit = preferences[Keys.DEFAULT_OPEN_EDIT] ?: false,
                isHapticEnabled = preferences[Keys.IS_HAPTIC] ?: true,
                isBiometricEnabled = preferences[Keys.IS_BIOMETRIC] ?: false,
                isSecureMode = preferences[Keys.IS_SECURE_MODE] ?: false,
                showAddCategoryButton = preferences[Keys.SHOW_ADD_CATEGORY_BUTTON] ?: true,
                is24HourFormat = preferences[Keys.IS_24_HOUR_FORMAT] ?: false,
                dateFormat = preferences[Keys.DATE_FORMAT] ?: "dd MMM"
            )
        }

    suspend fun setGridView(isGrid: Boolean) {
        dataStore.edit { it[Keys.IS_GRID_VIEW] = isGrid }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_DARK_MODE] = enabled }
    }

    suspend fun setTrueBlack(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_TRUE_BLACK] = enabled }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_DYNAMIC_COLOR] = enabled }
    }

    suspend fun setDefaultOpenInEdit(enabled: Boolean) {
        dataStore.edit { it[Keys.DEFAULT_OPEN_EDIT] = enabled }
    }

    suspend fun setHaptic(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_HAPTIC] = enabled }
    }

    suspend fun setBiometric(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_BIOMETRIC] = enabled }
    }

    suspend fun setSecureMode(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_SECURE_MODE] = enabled }
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            val keepAddButton = preferences[Keys.SHOW_ADD_CATEGORY_BUTTON] ?: true
            preferences.clear()
            preferences[Keys.SHOW_ADD_CATEGORY_BUTTON] = keepAddButton
        }
    }

    suspend fun setShowAddCategoryButton(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_ADD_CATEGORY_BUTTON] = show }
    }

    suspend fun setTimeFormat(is24Hour: Boolean) {
        dataStore.edit { it[Keys.IS_24_HOUR_FORMAT] = is24Hour }
    }

    suspend fun setDateFormat(format: String) {
        dataStore.edit { it[Keys.DATE_FORMAT] = format }
    }
}