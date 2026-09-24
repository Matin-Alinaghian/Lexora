package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.WordRepository
import com.lexora.app.data.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditWordUiState(
    val englishWord: String = "",
    val persianMeaning: String = "",
    val pronunciation: String = "",
    val example: String = "",
    val exampleTranslation: String = "",
    val wordType: String = "",
    val category: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val wordFamily: String = "",
    val personalNote: String = "",
    val level: String = "",
    val tags: String = "",
    val isInLeitner: Boolean = false,
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val existingLists: List<String> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val wordId: Long = savedStateHandle["wordId"] ?: 0L

    private val _uiState = MutableStateFlow(AddEditWordUiState(isEditing = wordId > 0))
    val uiState: StateFlow<AddEditWordUiState> = _uiState.asStateFlow()

    init {
                viewModelScope.launch {
            wordRepository.getAllCategories().collect { lists ->
                _uiState.update { it.copy(existingLists = lists) }
            }
        }

        if (wordId > 0) {
            viewModelScope.launch {
                wordRepository.getWordById(wordId)?.let { word ->
                    _uiState.update {
                        it.copy(
                            englishWord = word.englishWord,
                            persianMeaning = word.persianMeaning,
                            pronunciation = word.pronunciation,
                            example = word.example,
                            exampleTranslation = word.exampleTranslation,
                            wordType = word.wordType,
                            category = word.category,
                            synonyms = word.synonyms,
                            antonyms = word.antonyms,
                            wordFamily = word.wordFamily,
                            personalNote = word.personalNote,
                            level = word.level,
                            tags = word.tags,
                            isInLeitner = word.isInLeitner
                        )
                    }
                }
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.update { state ->
            when (field) {
                "englishWord" -> state.copy(englishWord = value)
                "persianMeaning" -> state.copy(persianMeaning = value)
                "pronunciation" -> state.copy(pronunciation = value)
                "example" -> state.copy(example = value)
                "exampleTranslation" -> state.copy(exampleTranslation = value)
                "wordType" -> state.copy(wordType = value)
                "category" -> state.copy(category = value)
                "synonyms" -> state.copy(synonyms = value)
                "antonyms" -> state.copy(antonyms = value)
                "wordFamily" -> state.copy(wordFamily = value)
                "personalNote" -> state.copy(personalNote = value)
                "level" -> state.copy(level = value)
                "tags" -> state.copy(tags = value)
                "isInLeitner" -> state.copy(isInLeitner = value == "true")
                else -> state
            }
        }
    }

    fun autoFillFromDictionary() {
        val word = _uiState.value.englishWord
        if (word.isBlank()) return

        viewModelScope.launch {
                        dictionaryRepository.searchDictionary(word).first().let { results ->
                val result = results.firstOrNull { it.word.equals(word, ignoreCase = true) } ?: results.firstOrNull()
                result?.let { res ->
                    _uiState.update { state ->
                        state.copy(
                            persianMeaning = res.persianTranslation.ifBlank { state.persianMeaning },
                            pronunciation = res.phonetic.ifBlank { state.pronunciation },
                            wordType = res.partOfSpeech.ifBlank { state.wordType },
                            level = res.level.ifBlank { state.level },
                            example = res.examples.firstOrNull() ?: state.example,
                            synonyms = res.synonyms.joinToString(", ").ifBlank { state.synonyms },
                            antonyms = res.antonyms.joinToString(", ").ifBlank { state.antonyms }
                        )
                    }
                }
            }
        }
    }

    fun saveWord() {
        val state = _uiState.value
        if (state.englishWord.isBlank() || state.persianMeaning.isBlank()) {
            _uiState.update { it.copy(error = "English word and Persian meaning are required") }
            return
        }

        viewModelScope.launch {
            val existing = if (state.isEditing) wordRepository.getWordById(wordId) else null
            val word = WordEntity(
                id = if (state.isEditing) wordId else 0,
                englishWord = state.englishWord.trim(),
                persianMeaning = state.persianMeaning.trim(),
                pronunciation = state.pronunciation.trim(),
                example = state.example.trim(),
                exampleTranslation = state.exampleTranslation.trim(),
                wordType = state.wordType.trim(),
                category = state.category.trim(),
                synonyms = state.synonyms.trim(),
                antonyms = state.antonyms.trim(),
                wordFamily = state.wordFamily.trim(),
                personalNote = state.personalNote.trim(),
                level = state.level.trim(),
                tags = state.tags.trim(),
                isInLeitner = existing?.isInLeitner ?: true,
                leitnerBox = existing?.leitnerBox ?: 1,
                nextReviewDate = existing?.nextReviewDate
                    ?: System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                xpEarned = existing?.xpEarned ?: 0,
                updatedAt = System.currentTimeMillis()
            )

            wordRepository.insertWord(word)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun renameList(oldName: String, newName: String) {
        viewModelScope.launch {
            wordRepository.updateCategoryName(oldName, newName)
        }
    }
}
