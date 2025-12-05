package com.openappslabs.jotter.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note

/**
 * Defines the main access point for the Room database.
 */
@Database(
    entities = [Note::class, Category::class],
    version = 4, // ✨ UPDATED: Version must be incremented
    exportSchema = false // ✨ FIX: Set to false to avoid build warnings
)
abstract class JotterDatabase : RoomDatabase() {

    // Abstract function to get the DAO for Notes
    abstract fun noteDao(): NoteDao

    // Abstract function to get the DAO for Categories
    abstract fun categoryDao(): CategoryDao
}