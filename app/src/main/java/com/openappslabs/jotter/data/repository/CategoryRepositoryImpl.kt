/*
 * Copyright (c) 2026 Open Apps Labs
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

import androidx.room.withTransaction
import com.openappslabs.jotter.data.model.Category
import com.openappslabs.jotter.data.source.CategoryDao
import com.openappslabs.jotter.data.source.JotterDatabase
import com.openappslabs.jotter.data.source.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val noteDao: NoteDao,
    private val database: JotterDatabase
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    override suspend fun insertCategory(name: String) {
        val formatted = Category.format(name)
        if (formatted.isNotBlank() && !Category.isReserved(formatted)) {
            categoryDao.insertCategory(Category(name = formatted))
        }
    }

    override suspend fun renameCategory(oldName: String, newName: String) {
        val formatted = Category.format(newName)
        if (formatted.isNotBlank() && !Category.isReserved(formatted) && oldName != formatted) {
            database.withTransaction {
                categoryDao.updateCategoryName(oldName, formatted)
                noteDao.updateNoteCategories(oldName, formatted)
            }
        }
    }

    override suspend fun deleteCategoryByName(name: String) {
        categoryDao.deleteCategoryByName(name)
    }

    override suspend fun clearCategoryReferences(categoryName: String) {
        noteDao.updateNoteCategories(categoryName, "")
    }
}