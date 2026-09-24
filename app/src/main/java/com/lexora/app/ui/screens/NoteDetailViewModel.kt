package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.NoteEntity
import com.lexora.app.data.repository.NoteRepository
import com.lexora.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val note: NoteEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    val ttsManager: TtsManager
) : ViewModel() {

    private val noteId: Long = savedStateHandle["noteId"] ?: 0L

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.getNoteByIdFlow(noteId).collect { note ->
                _uiState.update { it.copy(note = note, isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.note?.let { note ->
                noteRepository.updateNote(note.copy(isFavorite = !note.isFavorite))
            }
        }
    }

    fun toggleLeitner() {
        viewModelScope.launch {
            _uiState.value.note?.let { note ->
                val nextReview = if (!note.isInLeitner) {
                    System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
                } else 0L
                
                noteRepository.updateNote(
                    note.copy(
                        isInLeitner = !note.isInLeitner,
                        leitnerBox = if (!note.isInLeitner) 1 else 0,
                        nextReviewDate = nextReview
                    )
                )
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            noteRepository.deleteNoteById(noteId)
        }
    }

    fun speakText(text: String) {
        ttsManager.speak(text)
    }
}
