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

    // 1. READ: Categories are streamed from the database (persisted Category Entity)
    val categories: StateFlow<List<String>> = categoryRepository.getAllCategories()
        .map { categoryList ->
            // Map the Category Entity objects back to a simple list of names (Strings)
            categoryList.map { it.name }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // --- ACTIONS ---

    fun addCategory(newCategory: String) {
        val trimmed = newCategory.trim()
        if (trimmed.isNotBlank() && !categories.value.contains(trimmed)) {
            viewModelScope.launch {
                // 2. ADD: Insert the category into the separate table
                categoryRepository.insertCategory(trimmed)
            }
        }
    }

    fun removeCategory(category: String) {
        viewModelScope.launch {
            // 1. Clean up references in Notes (set category field to empty for affected notes)
            // This is a crucial transaction before deleting the category itself.
            categoryRepository.clearCategoryReferences(category, notesRepository.getAllNotes())

            // 2. Delete the category entity
            categoryRepository.deleteCategoryByName(category)
        }
    }

    fun reorderCategories(fromIndex: Int, toIndex: Int) {
        // NOTE: This feature remains non-functional for persistence due to the database structure.
        // The list is always displayed alphabetically by the DAO query.
    }
}