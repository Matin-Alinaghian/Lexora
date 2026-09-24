package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.ReviewRepository
import com.lexora.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MistakesUiState(
    val mistakeWords: List<WordEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MistakesViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MistakesUiState())
    val uiState: StateFlow<MistakesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
                        reviewRepository.deleteOrphanedMistakes()
            reviewRepository.getMistakeWordIds().collect { wordIds ->
                val words = mutableListOf<WordEntity>()
                wordIds.forEach { id ->
                    wordRepository.getWordById(id)?.let { words.add(it) }
                }
                _uiState.update { it.copy(mistakeWords = words, isLoading = false) }
            }
        }
    }

    
    fun markAsKnown(wordId: Long) {
        viewModelScope.launch {
            reviewRepository.deleteMistakesByWordId(wordId)
        }
    }
}
