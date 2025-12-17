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

package com.openappslabs.jotter.navigation

object AppRoutes {
    const val HOME = "home"
    const val SETTINGS = "settings"

    const val ARCHIVE = "archive"
    const val TRASH = "trash"

    const val ADD_CATEGORY = "add_category"
    const val BACKUP_RESTORE = "backup_restore"
    const val PRIVACY_POLICY = "privacy_policy"
    const val ABOUT = "about"

    const val NOTE_ID_KEY = "noteId"

    const val NOTE_DETAIL = "note_detail"

    const val NOTE_DETAIL_ROUTE_WITH_ARGS = "$NOTE_DETAIL/{$NOTE_ID_KEY}"
}