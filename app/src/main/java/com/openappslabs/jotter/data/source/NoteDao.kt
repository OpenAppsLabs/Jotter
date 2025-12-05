package com.openappslabs.jotter.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openappslabs.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Note entity.
 * Defines the methods for interacting with the 'notes' table.
 */
@Dao
interface NoteDao {

    // Get all active notes (non-archived, non-trashed), pinned notes first
    @Query("SELECT * FROM notes WHERE isArchived = 0 AND isTrashed = 0 ORDER BY isPinned DESC, updatedTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    // Get a single note by ID
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    // Insert a new note (or replace if conflict by ID)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long // Returns the new row ID

    // Update an existing note
    @Update
    suspend fun update(note: Note)

    // Delete a note permanently
    @Delete
    suspend fun delete(note: Note)

    // --- Archive/Trash Operations ---

    // Get all archived notes
    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isTrashed = 0 ORDER BY updatedTime DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    // Get all trashed notes
    @Query("SELECT * FROM notes WHERE isTrashed = 1 ORDER BY updatedTime DESC")
    fun getTrashedNotes(): Flow<List<Note>>

    // Move all notes in trash to permanent deletion (for Empty Trash)
    @Query("DELETE FROM notes WHERE isTrashed = 1")
    suspend fun emptyTrash()

    // Get a Flow of all unique, non-blank categories used in notes
    @Query("SELECT DISTINCT category FROM notes WHERE category IS NOT NULL AND category != ''")
    fun getCategories(): Flow<List<String>>

    // ✨ NEW: Bulk Insert for Restore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    // ✨ NEW: Bulk Delete for Restore (Clears existing data)
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    // Snapshot for Backup
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesSync(): List<Note>

    // ✨ NEW: Unlock all notes
    @Query("UPDATE notes SET isLocked = 0 WHERE isLocked = 1")
    suspend fun unlockAllNotes()
}