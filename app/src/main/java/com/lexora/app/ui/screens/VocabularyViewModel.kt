package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyUiState(
    val words: List<WordEntity> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList()
)

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                wordRepository.getAllWords(),
                wordRepository.getAllCategories()
            ) { words, categories ->
                _uiState.update { it.copy(
                    words = filterWords(words, _uiState.value.searchQuery, _uiState.value.selectedCategory),
                    categories = categories,
                    isLoading = false
                ) }
            }.collect()
        }
    }

    private fun filterWords(words: List<WordEntity>, query: String, category: String?): List<WordEntity> {
        return words
            .filter { word ->
                (query.isBlank() || word.englishWord.contains(query, ignoreCase = true) || word.persianMeaning.contains(query, ignoreCase = true)) &&
                (category == null || word.category == category)
            }
            .sortedBy { it.englishWord.lowercase() }
    }

    fun searchWords(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshWords()
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshWords()
    }

    private fun refreshWords() {
        viewModelScope.launch {
            wordRepository.getAllWords().first().let { words ->
                _uiState.update { it.copy(
                    words = filterWords(words, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                ) }
            }
        }
    }

    fun toggleFavorite(word: WordEntity) {
        viewModelScope.launch {
            wordRepository.updateWord(word.copy(isFavorite = !word.isFavorite))
        }
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            wordRepository.deleteWordById(wordId)
        }
    }
}
