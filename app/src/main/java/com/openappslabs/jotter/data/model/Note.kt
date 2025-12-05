package com.openappslabs.jotter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the schema for the 'notes' table in the local Room database.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val createdTime: Long = System.currentTimeMillis(),
    val updatedTime: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false
)