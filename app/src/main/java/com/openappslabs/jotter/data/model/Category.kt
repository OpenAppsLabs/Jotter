package com.openappslabs.jotter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines the separate entity for managing categories (tags).
 * Uses the category name as the primary key since names must be unique.
 */
@Entity(tableName = "categories")
data class Category(
    // The category name itself serves as the unique identifier
    @PrimaryKey
    val name: String,
    // Optional: useful for displaying the list in order of creation/modification
    val createdTime: Long = System.currentTimeMillis()
)