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

data class SearchUiState(
    val words: List<WordEntity> = emptyList(),
    val grammar: List<GrammarEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val grammarRepository: GrammarRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { SearchUiState() }
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                wordRepository.searchWords(query).collect { words ->
                    _uiState.update { it.copy(words = words) }
                }
            }
            launch {
                grammarRepository.searchGrammar(query).collect { grammar ->
                    _uiState.update { it.copy(grammar = grammar) }
                }
            }
            launch {
                noteRepository.searchNotes(query).collect { notes ->
                    _uiState.update { it.copy(notes = notes, isLoading = false) }
                }
            }
        }
    }

    fun toggleWordFavorite(word: WordEntity) {
        viewModelScope.launch {
            wordRepository.updateWord(word.copy(isFavorite = !word.isFavorite))
        }
    }
}
