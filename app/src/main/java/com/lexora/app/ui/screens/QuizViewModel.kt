package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.dao.WordDao
import com.lexora.app.data.local.entity.MistakeEntity
import com.lexora.app.data.local.entity.ReviewEntity
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.data.repository.ReviewRepository
import com.lexora.app.data.repository.StudySessionRepository
import com.lexora.app.utils.StreakManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val word: WordEntity,
    val options: List<String>,      val correctIndex: Int,
    val questionType: QuestionType = QuestionType.EN_TO_FA
)

enum class QuestionType {
    EN_TO_FA,       FA_TO_EN    }

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isComplete: Boolean = false,
    val isLoading: Boolean = true,
    val streak: Int = 0,      val bestStreak: Int = 0
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val progress: Float get() = if (totalQuestions > 0) currentIndex.toFloat() / totalQuestions else 0f
    val accuracy: Int get() = if (currentIndex > 0) (correctCount * 100) / currentIndex else 0
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val reviewRepository: ReviewRepository,
    private val studySessionRepository: StudySessionRepository,
    private val streakManager: StreakManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var selectedCategory: String? = null
    private var selectedMode: String = "all"

    val allCategories: StateFlow<List<String>> = wordDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadQuestions()
    }

    fun setCategory(category: String?) {
        selectedCategory = category
        selectedMode = if (category == null) "all" else "category:$category"
        retry()
    }

    fun setMode(mode: String) {
        selectedMode = mode
        selectedCategory = null
        retry()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allWords = when {
                selectedMode == "leitner" -> wordDao.getLeitnerWordsDue(System.currentTimeMillis())
                selectedMode.startsWith("category:") -> {
                    val cat = selectedMode.substringAfter("category:")
                    wordDao.getAllWordsOnce().filter { it.category == cat }
                }
                else -> wordDao.getAllWordsOnce()
            }
            if (allWords.size < 4) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = emptyList()
                    )
                }
                return@launch
            }

                        val shuffled = allWords.shuffled().take(10)
            val questions = shuffled.map { word ->
                val type = if (Math.random() < 0.5) QuestionType.EN_TO_FA else QuestionType.FA_TO_EN
                val correctAnswer = when (type) {
                    QuestionType.EN_TO_FA -> word.persianMeaning
                    QuestionType.FA_TO_EN -> word.englishWord
                }

                                val wrongWords = allWords
                    .filter { it.id != word.id }
                    .shuffled()
                    .take(3)

                val wrongAnswers = wrongWords.map { wrong ->
                    when (type) {
                        QuestionType.EN_TO_FA -> wrong.persianMeaning
                        QuestionType.FA_TO_EN -> wrong.englishWord
                    }
                }

                val allOptions = (wrongAnswers + correctAnswer).shuffled()
                val correctIdx = allOptions.indexOf(correctAnswer)

                QuizQuestion(
                    word = word,
                    options = allOptions,
                    correctIndex = correctIdx,
                    questionType = type
                )
            }

            _uiState.update {
                it.copy(
                    questions = questions,
                    isLoading = false
                )
            }
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isAnswerRevealed) return

        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val isCorrect = index == question.correctIndex

        _uiState.update {
            it.copy(
                selectedOption = index,
                isAnswerRevealed = true,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                incorrectCount = if (isCorrect) it.incorrectCount else it.incorrectCount + 1,
                streak = if (isCorrect) it.streak + 1 else 0,
                bestStreak = if (isCorrect) maxOf(it.bestStreak, it.streak + 1) else it.bestStreak
            )
        }

                viewModelScope.launch {
            reviewRepository.insertReview(
                ReviewEntity(
                    wordId = question.word.id,
                    questionType = question.questionType.name,
                    isCorrect = isCorrect
                )
            )
            if (isCorrect) {
                studySessionRepository.recordStudyTime(0)
                streakManager.registerStudySession(wordsReviewed = 1, correctAnswers = 1)
            } else {
                streakManager.registerStudySession(wordsReviewed = 1, wrongAnswers = 1)
                reviewRepository.insertMistake(
                    MistakeEntity(
                        wordId = question.word.id,
                        question = question.word.englishWord,
                        correctAnswer = question.word.persianMeaning,
                        userAnswer = question.options[index],
                        questionType = question.questionType.name
                    )
                )
            }
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= state.totalQuestions) {
            _uiState.update { it.copy(isComplete = true) }
                        viewModelScope.launch {
                studySessionRepository.recordStudyTime(1)
                streakManager.registerStudySession(quizzesCompleted = 1, wordsReviewed = state.totalQuestions)
            }
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    selectedOption = null,
                    isAnswerRevealed = false
                )
            }
        }
    }

    fun retry() {
        _uiState.update {
            QuizUiState(isLoading = true)
        }
        loadQuestions()
    }
}
