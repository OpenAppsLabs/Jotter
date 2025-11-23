package com.openapps.jotter.ui.screens.notedetailscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openapps.jotter.data.model.Note
import com.openapps.jotter.data.repository.CategoryRepository
import com.openapps.jotter.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val noteId: Int? = savedStateHandle.get<Int>("noteId")

    data class UiState(
        val id: Int? = null,
        val title: String = "",
        val content: String = "",
        val category: String = "", // Empty string default
        val isPinned: Boolean = false,
        val isLocked: Boolean = false,
        val isArchived: Boolean = false,
        val isTrashed: Boolean = false,
        val lastEdited: Long = System.currentTimeMillis(),
        val isNotePersisted: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Expose the live list of categories for the CategorySheet
    val availableCategories: StateFlow<List<String>> = categoryRepository.getAllCategories()
        .map { categoryList -> categoryList.map { it.name } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        if (noteId != null && noteId != -1) {
            loadNote(noteId)
        } else {
            _uiState.update { it.copy(isNotePersisted = false, isLoading = false) }
        }
        observeCategoryCleanup() // ✨ Call the cleanup observer on initialization
    }

    // ✨ NEW FUNCTION: Observes available categories and resets note's category if it was deleted
    private fun observeCategoryCleanup() {
        viewModelScope.launch {
            availableCategories
                .collectLatest { categories -> // collectLatest handles flow lifecycle efficiently
                    val currentCategory = uiState.value.category

                    // If the note has a category AND that category is no longer in the master list,
                    // reset the note's category to empty.
                    if (currentCategory.isNotBlank() && !categories.contains(currentCategory)) {
                        _uiState.update {
                            it.copy(category = "")
                        }
                    }
                }
        }
    }

    // --- Data Loading ---

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            val note = notesRepository.getNoteById(id)
            if (note != null) {
                _uiState.update {
                    it.copy(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        category = note.category,
                        isPinned = note.isPinned,
                        isLocked = note.isLocked,
                        isArchived = note.isArchived,
                        isTrashed = note.isTrashed,
                        lastEdited = note.updatedTime,
                        isNotePersisted = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.value = UiState(isLoading = false)
            }
        }
    }

    // Helper function to save status changes (Pin/Lock) without triggering a full content save
    private fun saveNoteStatus() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState.id != null) {
                val updatedNote = Note(
                    id = currentState.id!!,
                    title = currentState.title,
                    content = currentState.content,
                    category = currentState.category,
                    isPinned = currentState.isPinned,
                    isLocked = currentState.isLocked,
                    isArchived = currentState.isArchived,
                    isTrashed = currentState.isTrashed,
                    updatedTime = currentState.lastEdited
                )
                notesRepository.updateNote(updatedNote)
            }
        }
    }

    // --- User Actions ---

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateContent(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
        saveNoteStatus()
    }

    fun toggleLock() {
        _uiState.update { it.copy(isLocked = !it.isLocked) }
        saveNoteStatus()
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Final VM logic provided:
            if (currentState.category.isNotBlank()) {
                categoryRepository.insertCategory(currentState.category)
            }

            val noteToSave = Note(
                id = currentState.id ?: 0,
                title = currentState.title,
                content = currentState.content,
                category = currentState.category,
                isPinned = currentState.isPinned,
                isLocked = currentState.isLocked,
                isArchived = currentState.isArchived,
                isTrashed = currentState.isTrashed
            )

            val idAfterSave: Int?

            if (currentState.isNotePersisted) {
                notesRepository.updateNote(noteToSave)
                idAfterSave = currentState.id
            } else {
                val newIdLong = notesRepository.addNote(noteToSave)
                idAfterSave = newIdLong.toInt()
            }

            // On success: Update local state with new ID and persistence status
            idAfterSave?.let { freshNoteId ->
                notesRepository.getNoteById(freshNoteId)?.let { freshNote ->
                    _uiState.update {
                        it.copy(
                            id = freshNote.id,
                            isNotePersisted = true,
                            lastEdited = freshNote.updatedTime
                        )
                    }
                }
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            val note = uiState.value
            val noteToDelete = Note(id = note.id ?: 0)

            if (note.isTrashed) {
                notesRepository.deleteNote(noteToDelete)
            } else {
                notesRepository.trashNote(noteToDelete)
            }
        }
    }

    fun restoreNote() {
        viewModelScope.launch {
            notesRepository.restoreNote(
                Note(id = uiState.value.id ?: 0)
            )
        }
    }

    fun undoChanges() {
        if (noteId != null && noteId != -1) {
            loadNote(noteId)
        }
    }
}