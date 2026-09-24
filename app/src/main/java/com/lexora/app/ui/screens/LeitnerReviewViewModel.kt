package com.lexora.app.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.MistakeEntity
import com.lexora.app.data.local.entity.ReviewEntity
import com.lexora.app.data.repository.GrammarRepository
import com.lexora.app.data.repository.NoteRepository
import com.lexora.app.data.repository.ReviewRepository
import com.lexora.app.data.repository.StudySessionRepository
import com.lexora.app.data.repository.WordRepository
import com.lexora.app.utils.StreakManager
import com.lexora.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeitnerReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val grammarRepository: GrammarRepository,
    private val noteRepository: NoteRepository,
    private val reviewRepository: ReviewRepository,
    private val studySessionRepository: StudySessionRepository,
    private val streakManager: StreakManager,
    private val ttsManager: TtsManager
) : ViewModel() {

    val mode: String = savedStateHandle.get<String>("mode") ?: "all"

    private val categoryFilter: String =
        if (mode.startsWith("category:")) mode.substringAfter("category:") else ""

    private val isMistakesMode = mode == "mistakes"

    val allCategories: StateFlow<List<String>> = wordRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(LeitnerReviewState())
    val uiState: StateFlow<LeitnerReviewState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val words = when {
                isMistakesMode -> wordRepository.getWordsWithMistakes()
                categoryFilter.isNotBlank() -> wordRepository.getAllWords().first()
                    .filter { it.category == categoryFilter }
                else -> wordRepository.getRandomWords(QUICK_REVIEW_SIZE)
            }.map { LeitnerItem.Word(it) }

            val grammar = if (categoryFilter.isNotBlank()) {
                grammarRepository.getAllGrammar().first()
                    .filter { it.category == categoryFilter }
                    .map { LeitnerItem.Grammar(it) }
            } else {
                emptyList()
            }

            val notes = if (categoryFilter.isNotBlank()) {
                noteRepository.getAllNotes().first()
                    .filter { it.category == categoryFilter }
                    .map { LeitnerItem.Note(it) }
            } else {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    items = (words + grammar + notes).shuffled(),
                    isLoading = false,
                    currentIndex = 0,
                    isFlipped = false,
                    correctCount = 0,
                    incorrectCount = 0,
                    isComplete = false,
                    categoryName = categoryFilter
                )
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun markCorrect() {
        val state = _uiState.value
        val item = state.currentItem ?: return

        viewModelScope.launch {
            if (isMistakesMode) clearMistakes(item)
            registerAnswer(item, correct = true)
            studySessionRepository.addXp(xpFor(item))
            streakManager.registerStudySession(wordsReviewed = 1, correctAnswers = 1)
        }

        moveToNext(state.copy(correctCount = state.correctCount + 1))
    }

    fun markIncorrect() {
        val state = _uiState.value
        val item = state.currentItem ?: return

        viewModelScope.launch {
            registerAnswer(item, correct = false)
            if (!isMistakesMode) registerMistake(item)
            streakManager.registerStudySession(wordsReviewed = 1, wrongAnswers = 1)
        }

        moveToNext(state.copy(incorrectCount = state.incorrectCount + 1))
    }

    private fun xpFor(item: LeitnerItem): Int = when (item) {
        is LeitnerItem.Word -> 15
        is LeitnerItem.Grammar -> 20
        is LeitnerItem.Note -> 10
    }

    private suspend fun registerAnswer(item: LeitnerItem, correct: Boolean) {
        when (item) {
            is LeitnerItem.Word -> reviewRepository.insertReview(
                ReviewEntity(wordId = item.entity.id, questionType = "review", isCorrect = correct)
            )
            is LeitnerItem.Grammar -> reviewRepository.insertReview(
                ReviewEntity(wordId = 0, questionType = "review_grammar", isCorrect = correct)
            )
            is LeitnerItem.Note -> reviewRepository.insertReview(
                ReviewEntity(wordId = 0, questionType = "review_note", isCorrect = correct)
            )
        }
    }

    private suspend fun registerMistake(item: LeitnerItem) {
        when (item) {
            is LeitnerItem.Word -> reviewRepository.insertMistake(
                MistakeEntity(
                    wordId = item.entity.id,
                    question = item.entity.englishWord,
                    correctAnswer = item.entity.persianMeaning,
                    userAnswer = "",
                    questionType = "review"
                )
            )
            is LeitnerItem.Grammar -> reviewRepository.insertMistake(
                MistakeEntity(
                    grammarId = item.entity.id,
                    question = item.entity.title,
                    correctAnswer = item.entity.explanation,
                    userAnswer = "",
                    questionType = "review"
                )
            )
            is LeitnerItem.Note -> reviewRepository.insertMistake(
                MistakeEntity(
                    noteId = item.entity.id,
                    question = item.entity.title,
                    correctAnswer = item.entity.content,
                    userAnswer = "",
                    questionType = "review"
                )
            )
        }
    }

    private suspend fun clearMistakes(item: LeitnerItem) {
        when (item) {
            is LeitnerItem.Word -> reviewRepository.deleteMistakesByWordId(item.entity.id)
            is LeitnerItem.Grammar -> reviewRepository.deleteMistakesByGrammarId(item.entity.id)
            is LeitnerItem.Note -> reviewRepository.deleteMistakesByNoteId(item.entity.id)
        }
    }

    private fun moveToNext(state: LeitnerReviewState) {
        if (state.currentIndex + 1 >= state.items.size) {
            _uiState.update { state.copy(isComplete = true, isFlipped = false) }
            viewModelScope.launch {
                studySessionRepository.recordStudyTime(1)
                streakManager.registerStudySession(quizzesCompleted = 1)
            }
        } else {
            _uiState.update { state.copy(currentIndex = state.currentIndex + 1, isFlipped = false) }
        }
    }

    fun retry() {
        loadItems()
    }

    fun speak(text: String) {
        ttsManager.speak(text)
    }

    companion object {
        private const val QUICK_REVIEW_SIZE = 30
    }
}
