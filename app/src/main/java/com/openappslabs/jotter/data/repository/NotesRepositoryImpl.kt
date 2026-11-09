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

package com.openappslabs.jotter.data.repository

import androidx.room.withTransaction
import com.openappslabs.jotter.data.model.BackupData
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.source.CategoryDao
import com.openappslabs.jotter.data.source.JotterDatabase
import com.openappslabs.jotter.data.source.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val database: JotterDatabase
) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getTrashedNotes(): Flow<List<Note>> = noteDao.getTrashedNotes()
    override suspend fun getNoteById(noteId: Int): Note? = noteDao.getNoteById(noteId)

    override suspend fun addNote(note: Note): Long {
        return noteDao.insert(note.copy(updatedTime = System.currentTimeMillis()))
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.copy(updatedTime = System.currentTimeMillis()))
    }

    override suspend fun trashNote(note: Note) {
        noteDao.updateNoteStatus(
            noteId = note.id,
            isArchived = false,
            isTrashed = true,
            updatedTime = System.currentTimeMillis()
        )
    }

    override suspend fun restoreNote(note: Note) {
        noteDao.updateNoteStatus(
            noteId = note.id,
            isArchived = false,
            isTrashed = false,
            updatedTime = System.currentTimeMillis()
        )
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override fun getCategories(): Flow<List<String>> = categoryDao.getAllCategoryNames()

    override suspend fun getBackupData(): BackupData {
        val notes = noteDao.getAllNotesSync()
        val categories = categoryDao.getAllCategoriesSync()
        return BackupData(notes, categories)
    }

    override suspend fun restoreBackupData(backupData: BackupData) {
        database.withTransaction {
            noteDao.deleteAllNotes()
            categoryDao.deleteAllCategories()
            val sanitizedNotes = backupData.notes.map { it.copy(isArchived = false) }
            noteDao.insertAll(sanitizedNotes)
            categoryDao.insertAll(backupData.categories)
        }
    }

    override suspend fun clearAllDatabaseData() {
        database.withTransaction {
            noteDao.deleteAllNotes()
            categoryDao.deleteAllCategories()
        }
    }

    override suspend fun unlockAllNotes() {
        noteDao.unlockAllNotes()
    }
}
