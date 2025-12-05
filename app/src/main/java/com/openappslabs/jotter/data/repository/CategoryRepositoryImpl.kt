package com.openappslabs.jotter.data.repository

import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.source.CategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Concrete implementation of the CategoryRepository interface.
 * Handles category CRUD using CategoryDao and note cleanup using NotesRepository.
 */
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val notesRepository: NotesRepository // Used for cleaning up note references
) : CategoryRepository {

    // --- Read Operations ---
    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    // --- Write/Delete Operations ---
    override suspend fun insertCategory(name: String) {
        val trimmed = name.trim()

        // ✨ FIX: Validation check placed inside the Repository method as a final safeguard
        if (trimmed.isNotBlank()) {
            val category = Category(name = trimmed)
            categoryDao.insertCategory(category)
        }
    }

    override suspend fun deleteCategoryByName(name: String) {
        // Delete the Category entity itself
        categoryDao.deleteCategoryByName(name)

        // NOTE: The UI/ViewModel is responsible for calling clearCategoryReferences
        // to update the related Notes before or after this call.
    }

    // --- Special Cleanup Function ---
    override suspend fun clearCategoryReferences(categoryName: String, notesFlow: Flow<List<Note>>) {
        // This executes a cleanup transaction: finding all notes with the deleted category
        // and setting their category field to empty ("").

        // 1. Get the current list of notes once
        val notesToUpdate = notesFlow.first()

        // 2. Filter and update references
        notesToUpdate
            .filter { it.category == categoryName }
            .forEach { note ->
                // Update the note's category to empty string ("")
                notesRepository.updateNote(note.copy(category = ""))
            }
    }
}