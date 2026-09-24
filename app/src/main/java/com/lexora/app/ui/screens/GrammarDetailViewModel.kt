package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.GrammarEntity
import com.lexora.app.data.repository.GrammarRepository
import com.lexora.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrammarDetailUiState(
    val grammar: GrammarEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class GrammarDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val grammarRepository: GrammarRepository,
    val ttsManager: TtsManager
) : ViewModel() {

    private val grammarId: Long = savedStateHandle["grammarId"] ?: 0L

    private val _uiState = MutableStateFlow(GrammarDetailUiState())
    val uiState: StateFlow<GrammarDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            grammarRepository.getGrammarByIdFlow(grammarId).collect { grammar ->
                _uiState.update { it.copy(grammar = grammar, isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.grammar?.let { grammar ->
                grammarRepository.updateGrammar(grammar.copy(isFavorite = !grammar.isFavorite))
            }
        }
    }

    fun toggleLeitner() {
        viewModelScope.launch {
            _uiState.value.grammar?.let { grammar ->
                val nextReview = if (!grammar.isInLeitner) {
                    System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
                } else 0L
                
                grammarRepository.updateGrammar(
                    grammar.copy(
                        isInLeitner = !grammar.isInLeitner,
                        leitnerBox = if (!grammar.isInLeitner) 1 else 0,
                        nextReviewDate = nextReview
                    )
                )
            }
        }
    }

    fun deleteGrammar() {
        viewModelScope.launch {
            grammarRepository.deleteGrammarById(grammarId)
        }
    }

    fun speakText(text: String) {
        ttsManager.speak(text)
    }
}
