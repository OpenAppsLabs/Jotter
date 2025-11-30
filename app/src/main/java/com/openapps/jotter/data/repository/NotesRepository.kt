package com.openapps.jotter.data.repository

import com.openapps.jotter.data.model.Category // ✨ CRITICAL MISSING IMPORT
import com.openapps.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for data operations related to Note entities.
 */
interface NotesRepository {

    // --- Read Operations ---
    fun getAllNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    suspend fun getNoteById(noteId: Int): Note?

    // Category Read
    fun getCategories(): Flow<List<String>>

    // --- Write/Update Operations ---
    suspend fun addNote(note: Note): Long
    suspend fun updateNote(note: Note)

    // Helper functions for status changes
    suspend fun archiveNote(note: Note)
    suspend fun unarchiveNote(note: Note)
    suspend fun trashNote(note: Note)
    suspend fun restoreNote(note: Note)

    // --- Delete Operations ---
    suspend fun deleteNote(note: Note)
    suspend fun emptyTrash()

    // --- Backup & Restore ---
    suspend fun getBackupData(): Pair<List<Note>, List<Category>>
    suspend fun restoreBackupData(notes: List<Note>, categories: List<Category>)

    suspend fun clearAllDatabaseData()

    suspend fun unlockAllNotes() // ✨ ADDED
}