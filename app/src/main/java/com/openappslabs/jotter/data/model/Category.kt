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

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Locale

@Immutable
@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val name: String,
    val createdTime: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_LENGTH = 15

        val RESERVED_NAMES = setOf(
            "all",
            "pinned",
            "locked",
            "uncategorized"
        )

        fun format(name: String): String {
            val trimmed = name.trim().filter { !it.isWhitespace() }
            if (trimmed.isEmpty()) return ""
            val capped = if (trimmed.length > MAX_LENGTH) trimmed.take(MAX_LENGTH) else trimmed
            return capped.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

        fun isReserved(name: String): Boolean {
            return format(name).lowercase() in RESERVED_NAMES
        }
    }
}