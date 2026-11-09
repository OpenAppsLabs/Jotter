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

package com.openappslabs.jotter.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoutes {

    @Serializable
    data object Home : AppRoutes

    @Serializable
    data object Settings : AppRoutes

    @Serializable
    data object Trash : AppRoutes

    @Serializable
    data object AddCategory : AppRoutes

    @Serializable
    data object DataManagement : AppRoutes

    @Serializable
    data object PrivacyPolicy : AppRoutes

    @Serializable
    data object About : AppRoutes

    @Serializable
    data object AppearanceScreen : AppRoutes

    @Serializable
    data object MiscellaneousScreen : AppRoutes

    @Serializable
    data object SecurityScreen : AppRoutes

    @Serializable
    data class NoteDetail(
        val noteId: Int = -1,
        val category: String? = null
    ) : AppRoutes
}
