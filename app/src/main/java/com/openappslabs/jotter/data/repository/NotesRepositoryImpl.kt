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

import androidx.room.withTransaction
import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.source.CategoryDao
import com.openappslabs.jotter.data.source.JotterDatabase
import com.openappslabs.jotter.data.source.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val database: JotterDatabase // ✨ NEW: Inject Database for Transactions
) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()
    override fun getTrashedNotes(): Flow<List<Note>> = noteDao.getTrashedNotes()

    override suspend fun getNoteById(noteId: Int): Note? = noteDao.getNoteById(noteId)

    override suspend fun addNote(note: Note): Long {
        return noteDao.insert(note.copy(updatedTime = System.currentTimeMillis()))
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.copy(updatedTime = System.currentTimeMillis()))
    }

    override suspend fun archiveNote(note: Note) {
        val existingNote = noteDao.getNoteById(note.id)
        if (existingNote != null) {
            noteDao.update(existingNote.copy(
                isArchived = true,
                isTrashed = false,
                updatedTime = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun unarchiveNote(note: Note) {
        val existingNote = noteDao.getNoteById(note.id)
        if (existingNote != null) {
            noteDao.update(existingNote.copy(
                isArchived = false,
                isTrashed = false,
                updatedTime = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun trashNote(note: Note) {
        val existingNote = noteDao.getNoteById(note.id)
        if (existingNote != null) {
            noteDao.update(existingNote.copy(
                isTrashed = true,
                isArchived = false,
                updatedTime = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun restoreNote(note: Note) {
        val existingNote = noteDao.getNoteById(note.id)
        if (existingNote != null) {
            noteDao.update(existingNote.copy(
                isTrashed = false,
                isArchived = false,
                updatedTime = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override fun getCategories(): Flow<List<String>> = noteDao.getCategories()

    override suspend fun getBackupData(): Pair<List<Note>, List<Category>> {
        val notes = noteDao.getAllNotesSync()
        val categories = categoryDao.getAllCategoriesSync()
        return Pair(notes, categories)
    }

    override suspend fun restoreBackupData(notes: List<Note>, categories: List<Category>) {
        database.withTransaction {
            noteDao.deleteAllNotes()
            categoryDao.deleteAllCategories()

            noteDao.insertAll(notes)
            categoryDao.insertAll(categories)
        }
    }

    override suspend fun clearAllDatabaseData() {
        noteDao.deleteAllNotes()
        categoryDao.deleteAllCategories()
    }

    override suspend fun unlockAllNotes() {
        noteDao.unlockAllNotes()
    }
}