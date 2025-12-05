package com.openappslabs.jotter.data.model

/**
 * Represents the structure of the backup file.
 * It holds lists of all notes and categories to be serialized into JSON.
 */
data class BackupData(
    val notes: List<Note>,
    val categories: List<Category>
)