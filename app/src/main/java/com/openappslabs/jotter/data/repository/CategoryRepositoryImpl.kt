/*
 * Copyright (c) 2025 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.openappslabs.jotter.data.repository

import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.model.Note
import com.openappslabs.jotter.data.source.CategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val notesRepository: NotesRepository // Used for cleaning up note references
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    override suspend fun insertCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank()) {
            val category = Category(name = trimmed)
            categoryDao.insertCategory(category)
        }
    }

    override suspend fun deleteCategoryByName(name: String) {
        categoryDao.deleteCategoryByName(name)
    }

    override suspend fun clearCategoryReferences(categoryName: String, notesFlow: Flow<List<Note>>) {
        val notesToUpdate = notesFlow.first()
        notesToUpdate
            .filter { it.category == categoryName }
            .forEach { note ->
                notesRepository.updateNote(note.copy(category = ""))
            }
    }
}