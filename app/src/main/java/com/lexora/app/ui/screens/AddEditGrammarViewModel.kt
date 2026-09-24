package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.GrammarEntity
import com.lexora.app.data.repository.GrammarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditGrammarUiState(
    val title: String = "",
    val category: String = "",
    val explanation: String = "",
    val positiveForm: String = "",
    val negativeForm: String = "",
    val questionForm: String = "",
    val examples: String = "",
    val tips: String = "",
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val existingLists: List<String> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class AddEditGrammarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val grammarRepository: GrammarRepository
) : ViewModel() {

    private val grammarId: Long = savedStateHandle["grammarId"] ?: 0L

    private val _uiState = MutableStateFlow(AddEditGrammarUiState(isEditing = grammarId > 0))
    val uiState: StateFlow<AddEditGrammarUiState> = _uiState.asStateFlow()

    init {
                viewModelScope.launch {
            grammarRepository.getAllCategories().collect { lists ->
                _uiState.update { it.copy(existingLists = lists) }
            }
        }

        if (grammarId > 0) {
            viewModelScope.launch {
                grammarRepository.getGrammarById(grammarId)?.let { grammar ->
                    _uiState.update {
                        it.copy(
                            title = grammar.title,
                            category = grammar.category,
                            explanation = grammar.explanation,
                            positiveForm = grammar.positiveForm,
                            negativeForm = grammar.negativeForm,
                            questionForm = grammar.questionForm,
                            examples = grammar.examples,
                            tips = grammar.tips
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
                "category" -> state.copy(category = value)
                "explanation" -> state.copy(explanation = value)
                "positiveForm" -> state.copy(positiveForm = value)
                "negativeForm" -> state.copy(negativeForm = value)
                "questionForm" -> state.copy(questionForm = value)
                "examples" -> state.copy(examples = value)
                "tips" -> state.copy(tips = value)
                else -> state
            }
        }
    }

    fun saveGrammar() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Title is required") }
            return
        }

        viewModelScope.launch {
            val grammar = GrammarEntity(
                id = if (state.isEditing) grammarId else 0,
                title = state.title.trim(),
                category = state.category.trim(),
                explanation = state.explanation.trim(),
                positiveForm = state.positiveForm.trim(),
                negativeForm = state.negativeForm.trim(),
                questionForm = state.questionForm.trim(),
                examples = state.examples.trim(),
                tips = state.tips.trim(),
                updatedAt = System.currentTimeMillis()
            )

            grammarRepository.insertGrammar(grammar)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun renameList(oldName: String, newName: String) {
        viewModelScope.launch {
            grammarRepository.updateCategoryName(oldName, newName)
        }
    }
}
