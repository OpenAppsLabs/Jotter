package com.openapps.jotter.data.repository

import com.openapps.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for data operations related to Note entities.
 * The ViewModels depend on this interface, not the concrete implementation.
 */
interface NotesRepository {

    // --- Read Operations ---
    fun getAllNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    suspend fun getNoteById(noteId: Int): Note?

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

    fun getCategories(): Flow<List<String>>
}