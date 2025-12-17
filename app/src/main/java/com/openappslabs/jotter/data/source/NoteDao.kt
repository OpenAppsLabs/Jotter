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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openappslabs.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow
@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isArchived = 0 AND isTrashed = 0 ORDER BY isPinned DESC, updatedTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long // Returns the new row ID

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isTrashed = 0 ORDER BY updatedTime DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isTrashed = 1 ORDER BY updatedTime DESC")
    fun getTrashedNotes(): Flow<List<Note>>

    @Query("DELETE FROM notes WHERE isTrashed = 1")
    suspend fun emptyTrash()

    @Query("SELECT DISTINCT category FROM notes WHERE category IS NOT NULL AND category != ''")
    fun getCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesSync(): List<Note>

    @Query("UPDATE notes SET isLocked = 0 WHERE isLocked = 1")
    suspend fun unlockAllNotes()
}