package com.openappslabs.jotter.data.repository

import androidx.room.withTransaction
import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.source.CategoryDao
import com.openappslabs.jotter.data.source.JotterDatabase
import com.openappslabs.jotter.data.source.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of the NotesRepository interface, using the local Room DAO.
 */
class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val database: JotterDatabase // ✨ NEW: Inject Database for Transactions
) : NotesRepository {

    // --- Read Operations ---
    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()
    override fun getTrashedNotes(): Flow<List<Note>> = noteDao.getTrashedNotes()

    override suspend fun getNoteById(noteId: Int): Note? = noteDao.getNoteById(noteId)

    // --- Write/Update Operations ---
    override suspend fun addNote(note: Note): Long {
        // Automatically updates 'updatedTime' whenever a note is added/saved
        return noteDao.insert(note.copy(updatedTime = System.currentTimeMillis()))
    }

    override suspend fun updateNote(note: Note) {
        // Automatically updates 'updatedTime' whenever a note is updated
        noteDao.update(note.copy(updatedTime = System.currentTimeMillis()))
    }

    // Helper functions for status changes
    override suspend fun archiveNote(note: Note) {
        // 1. Fetch the full, current note from the database
        val existingNote = noteDao.getNoteById(note.id)
        if (existingNote != null) {
            // 2. Apply only the status change
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

    // --- Delete Operations ---
    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override fun getCategories(): Flow<List<String>> = noteDao.getCategories()

    // --- Backup & Restore Implementation ---

    override suspend fun getBackupData(): Pair<List<Note>, List<Category>> {
        val notes = noteDao.getAllNotesSync()
        val categories = categoryDao.getAllCategoriesSync()
        return Pair(notes, categories)
    }

    override suspend fun restoreBackupData(notes: List<Note>, categories: List<Category>) {
        // ✨ FIX: Use Transaction to prevent partial restores or data loss on crash
        database.withTransaction {
            // 1. Wipe existing data
            noteDao.deleteAllNotes()
            categoryDao.deleteAllCategories()

            // 2. Insert backup data
            noteDao.insertAll(notes)
            categoryDao.insertAll(categories)
        }
    }

    override suspend fun clearAllDatabaseData() {
        // These functions were added to your DAOs during the Backup/Restore setup
        noteDao.deleteAllNotes()
        categoryDao.deleteAllCategories()
    }

    override suspend fun unlockAllNotes() {
        noteDao.unlockAllNotes()
    }
}