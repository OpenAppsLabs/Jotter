package com.openappslabs.jotter.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// 1. The Model: Holds our settings data
data class UserPreferences(
    val isGridView: Boolean = false,
    val isDarkMode: Boolean = false,
    val isTrueBlackEnabled: Boolean = false,
    val isDynamicColor: Boolean = true,
    val defaultOpenInEdit: Boolean = false,
    val isHapticEnabled: Boolean = true,
    val isBiometricEnabled: Boolean = false,
    val isSecureMode: Boolean = false,
    val showAddCategoryButton: Boolean = true
)

// 2. The Repository: Handles saving/loading
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // Define the keys for storing data
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
    }

    // Read Data (Exposed as a Flow)
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
                showAddCategoryButton = preferences[Keys.SHOW_ADD_CATEGORY_BUTTON] ?: true
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
}