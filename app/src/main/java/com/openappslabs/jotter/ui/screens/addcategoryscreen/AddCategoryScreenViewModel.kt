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

package com.openappslabs.jotter.ui.screens.addcategoryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.data.repository.CategoryRepository
import com.openappslabs.jotter.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    val categories: StateFlow<List<String>> = categoryRepository.getAllCategories()
        .map { categoryList ->
            categoryList.map { it.name }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addCategory(newCategory: String) {
        val trimmed = newCategory.trim()
        if (trimmed.isNotBlank() && !categories.value.contains(trimmed)) {
            viewModelScope.launch {
                categoryRepository.insertCategory(trimmed)
            }
        }
    }

    fun removeCategory(category: String) {
        viewModelScope.launch {
            categoryRepository.clearCategoryReferences(category, notesRepository.getAllNotes())
            categoryRepository.deleteCategoryByName(category)
        }
    }
}