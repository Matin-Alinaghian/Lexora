package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.NoteEntity
import com.lexora.app.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList()
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                noteRepository.getAllNotes(),
                noteRepository.getAllCategories()
            ) { notes, categories ->
                _uiState.update { it.copy(
                    notes = filterNotes(notes, _uiState.value.searchQuery, _uiState.value.selectedCategory),
                    categories = categories,
                    isLoading = false
                ) }
            }.collect()
        }
    }

    private fun filterNotes(notes: List<NoteEntity>, query: String, category: String?): List<NoteEntity> {
        return notes.filter { note ->
            (query.isBlank() || note.title.contains(query, ignoreCase = true) || note.content.contains(query, ignoreCase = true)) &&
            (category == null || note.category == category)
        }
    }

    fun searchNotes(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshNotes()
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshNotes()
    }

    private fun refreshNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes().first().let { notes ->
                _uiState.update { it.copy(
                    notes = filterNotes(notes, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                ) }
            }
        }
    }

    fun toggleFavorite(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isFavorite = !note.isFavorite))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(id)
        }
    }
}
