package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.NoteEntity
import com.lexora.app.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditNoteUiState(
    val title: String = "",
    val content: String = "",
    val bulletPoints: String = "",
    val category: String = "",
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val existingLists: List<String> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val noteId: Long = savedStateHandle["noteId"] ?: 0L

    private val _uiState = MutableStateFlow(AddEditNoteUiState(isEditing = noteId > 0))
    val uiState: StateFlow<AddEditNoteUiState> = _uiState.asStateFlow()

    init {
                viewModelScope.launch {
            noteRepository.getAllCategories().collect { lists ->
                _uiState.update { it.copy(existingLists = lists) }
            }
        }

        if (noteId > 0) {
            viewModelScope.launch {
                noteRepository.getNoteById(noteId)?.let { note ->
                    _uiState.update {
                        it.copy(
                            title = note.title,
                            content = note.content,
                            bulletPoints = note.bulletPoints,
                            category = note.category
                        )
                    }
                }
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { state ->
            when (field) {
                "title" -> state.copy(title = value)
                "content" -> state.copy(content = value)
                "bulletPoints" -> state.copy(bulletPoints = value)
                "category" -> state.copy(category = value)
                else -> state
            }
        }
    }

    fun saveNote() {
        val state = _uiState.value
        if (state.title.isBlank() || state.content.isBlank()) {
            _uiState.update { it.copy(error = "Title and content are required") }
            return
        }

        viewModelScope.launch {
            val note = NoteEntity(
                id = if (state.isEditing) noteId else 0,
                title = state.title.trim(),
                content = state.content.trim(),
                bulletPoints = state.bulletPoints.trim(),
                category = state.category.trim(),
                updatedAt = System.currentTimeMillis()
            )

            noteRepository.insertNote(note)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun renameList(oldName: String, newName: String) {
        viewModelScope.launch {
            noteRepository.updateCategoryName(oldName, newName)
        }
    }
}
