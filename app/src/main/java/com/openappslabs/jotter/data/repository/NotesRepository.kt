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

import com.openappslabs.jotter.data.model.Category // ✨ CRITICAL MISSING IMPORT
import com.openappslabs.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow
interface NotesRepository {

    fun getAllNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    suspend fun getNoteById(noteId: Int): Note?

    fun getCategories(): Flow<List<String>>

    suspend fun addNote(note: Note): Long
    suspend fun updateNote(note: Note)

    suspend fun archiveNote(note: Note)
    suspend fun unarchiveNote(note: Note)
    suspend fun trashNote(note: Note)
    suspend fun restoreNote(note: Note)

    suspend fun deleteNote(note: Note)
    suspend fun emptyTrash()

    suspend fun getBackupData(): Pair<List<Note>, List<Category>>
    suspend fun restoreBackupData(notes: List<Note>, categories: List<Category>)

    suspend fun clearAllDatabaseData()

    suspend fun unlockAllNotes() // ✨ ADDED
}