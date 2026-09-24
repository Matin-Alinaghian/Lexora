package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.WordRepository
import com.lexora.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordDetailUiState(
    val word: WordEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    val ttsManager: TtsManager
) : ViewModel() {

    private val wordId: Long = savedStateHandle["wordId"] ?: 0L

    private val _uiState = MutableStateFlow(WordDetailUiState())
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            wordRepository.getWordByIdFlow(wordId).collect { word ->
                _uiState.update { it.copy(word = word, isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.word?.let { word ->
                wordRepository.updateWord(word.copy(isFavorite = !word.isFavorite))
            }
        }
    }

    fun toggleLeitner() {
        viewModelScope.launch {
            _uiState.value.word?.let { word ->
                val nextReview = if (!word.isInLeitner) {
                    System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
                } else 0L
                
                wordRepository.updateWord(
                    word.copy(
                        isInLeitner = !word.isInLeitner,
                        leitnerBox = if (!word.isInLeitner) 1 else 0,
                        nextReviewDate = nextReview
                    )
                )
            }
        }
    }

    fun deleteWord() {
        viewModelScope.launch {
            wordRepository.deleteWordById(wordId)
        }
    }

    fun speakText(text: String) {
        ttsManager.speak(text)
    }
}
