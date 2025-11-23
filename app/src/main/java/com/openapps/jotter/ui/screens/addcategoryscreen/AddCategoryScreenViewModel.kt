package com.openapps.jotter.ui.screens.addcategoryscreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddCategoryScreenViewModel @Inject constructor() : ViewModel() {

    // Internal mutable list acting as our data source
    private val _categories = MutableStateFlow(
        listOf("Personal", "Work", "Ideas", "Shopping", "Finance", "Health", "Travel")
    )
    // Public immutable flow for the UI
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // --- ACTIONS ---

    fun addCategory(newCategory: String) {
        val trimmed = newCategory.trim()
        if (trimmed.isNotBlank() && !_categories.value.contains(trimmed)) {
            // Create a new list with the added item at the END
            _categories.value = _categories.value + trimmed
        }
    }

    fun removeCategory(category: String) {
        // Create a new list without the removed item
        _categories.value = _categories.value - category
    }

    fun reorderCategories(fromIndex: Int, toIndex: Int) {
        val currentList = _categories.value.toMutableList()

        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)

            // Update the flow with the reordered list
            _categories.value = currentList
        }
    }
}