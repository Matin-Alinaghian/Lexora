package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.GrammarEntity
import com.lexora.app.data.repository.GrammarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrammarUiState(
    val grammarItems: List<GrammarEntity> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList()
)

@HiltViewModel
class GrammarViewModel @Inject constructor(
    private val grammarRepository: GrammarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrammarUiState())
    val uiState: StateFlow<GrammarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                grammarRepository.getAllGrammar(),
                grammarRepository.getAllCategories()
            ) { items, categories ->
                _uiState.update { it.copy(
                    grammarItems = filterItems(items, _uiState.value.searchQuery, _uiState.value.selectedCategory),
                    categories = categories,
                    isLoading = false
                ) }
            }.collect()
        }
    }

    private fun filterItems(items: List<GrammarEntity>, query: String, category: String?): List<GrammarEntity> {
        return items.filter { item ->
            (query.isBlank() || item.title.contains(query, ignoreCase = true) || item.explanation.contains(query, ignoreCase = true)) &&
            (category == null || item.category == category)
        }
    }

    fun searchGrammar(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshItems()
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshItems()
    }

    private fun refreshItems() {
        viewModelScope.launch {
            grammarRepository.getAllGrammar().first().let { items ->
                _uiState.update { it.copy(
                    grammarItems = filterItems(items, _uiState.value.searchQuery, _uiState.value.selectedCategory)
                ) }
            }
        }
    }

    fun toggleFavorite(grammar: GrammarEntity) {
        viewModelScope.launch {
            grammarRepository.updateGrammar(grammar.copy(isFavorite = !grammar.isFavorite))
        }
    }

    fun deleteGrammar(id: Long) {
        viewModelScope.launch {
            grammarRepository.deleteGrammarById(id)
        }
    }
}
