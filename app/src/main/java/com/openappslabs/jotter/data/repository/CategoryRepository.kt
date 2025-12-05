package com.openappslabs.jotter.data.repository

import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for managing Category entities.
 */
interface CategoryRepository {

    // --- Read Operations ---
    fun getAllCategories(): Flow<List<Category>>

    // --- Write/Delete Operations ---
    suspend fun insertCategory(name: String)
    suspend fun deleteCategoryByName(name: String)

    // --- Special Cleanup Function ---
    // This is required to find all Notes using the deleted category name
    // and reset their category field to empty ("") to prevent data inconsistencies.
    suspend fun clearCategoryReferences(categoryName: String, notesFlow: Flow<List<Note>>)
}