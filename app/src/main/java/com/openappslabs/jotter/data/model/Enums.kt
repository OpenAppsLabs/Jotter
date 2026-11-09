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

package com.openappslabs.jotter.data.model

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class SortDirection {
    ASCENDING, DESCENDING
}

enum class SortType {
    ALPHABETICAL, CREATED, LAST_UPDATED;

    fun next(): SortType {
        val values = entries.toTypedArray()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }
}

enum class BiometricAuthType(val authenticators: Int) {
    NONE(0),
    BIOMETRIC(BIOMETRIC_STRONG),
    DEVICE_CREDENTIAL(androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL);

    val isAvailable: Boolean get() = this != NONE
    val label: String get() = when (this) {
        NONE -> "None"
        BIOMETRIC -> "Biometric"
        DEVICE_CREDENTIAL -> "Device Credential"
    }
}

enum class BackupDialogType {
    NO_DATA_TO_EXPORT,
    EXPORT_SUCCESS,
    IMPORT_SUCCESS,
    ERROR
}
