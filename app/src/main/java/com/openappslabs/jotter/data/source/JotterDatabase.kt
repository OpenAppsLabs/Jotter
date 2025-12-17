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

package com.openappslabs.jotter.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note

@Database(
    entities = [Note::class, Category::class],
    version = 4,
    exportSchema = false
)
abstract class JotterDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun categoryDao(): CategoryDao
}