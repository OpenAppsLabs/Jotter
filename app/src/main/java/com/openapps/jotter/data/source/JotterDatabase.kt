package com.openapps.jotter.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openapps.jotter.data.model.Category // Import the new Category Entity
import com.openapps.jotter.data.model.Note

/**
 * Defines the main access point for the Room database.
 */
@Database(
    entities = [Note::class, Category::class], // ✨ UPDATED: Added Category Entity
    version = 2, // ✨ UPDATED: Version must be incremented
    exportSchema = false
)
abstract class JotterDatabase : RoomDatabase() {

    // Abstract function to get the DAO for Notes
    abstract fun noteDao(): NoteDao

    // Abstract function to get the DAO for Categories
    abstract fun categoryDao(): CategoryDao // ✨ ADDED: New DAO access
}