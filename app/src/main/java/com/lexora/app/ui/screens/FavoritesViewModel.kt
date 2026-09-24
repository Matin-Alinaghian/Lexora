package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.GrammarEntity
import com.lexora.app.data.local.entity.NoteEntity
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.GrammarRepository
import com.lexora.app.data.repository.NoteRepository
import com.lexora.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favoriteWords: List<WordEntity> = emptyList(),
    val favoriteGrammar: List<GrammarEntity> = emptyList(),
    val favoriteNotes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val grammarRepository: GrammarRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            wordRepository.getFavoriteWords().collect { words ->
                _uiState.update { it.copy(favoriteWords = words, isLoading = false) }
            }
        }
        viewModelScope.launch {
            grammarRepository.getFavoriteGrammar().collect { grammar ->
                _uiState.update { it.copy(favoriteGrammar = grammar) }
            }
        }
        viewModelScope.launch {
            noteRepository.getFavoriteNotes().collect { notes ->
                _uiState.update { it.copy(favoriteNotes = notes) }
            }
        }
    }

    fun toggleWordFavorite(word: WordEntity) {
        viewModelScope.launch {
            wordRepository.updateWord(word.copy(isFavorite = !word.isFavorite))
        }
    }
}
