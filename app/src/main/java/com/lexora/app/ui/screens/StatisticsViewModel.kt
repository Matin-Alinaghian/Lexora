package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.OverallStreakEntity
import com.lexora.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class StatisticsUiState(
    val weekStudyTime: Int = 0,
    val weekWords: Int = 0,
    val weekTests: Int = 0,
    val weekCorrect: Int = 0,
    val weekIncorrect: Int = 0,
    val accuracy: Int = 0,
    val totalWords: Int = 0,
    val mistakeCount: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val activeDays: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val studySessionRepository: StudySessionRepository,
    private val reviewRepository: ReviewRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    val uiState: StateFlow<StatisticsUiState> = combine(
        studySessionRepository.getStudyTimeBetweenDates(getWeekStart(), today),
        studySessionRepository.getWordsStudiedBetweenDates(getWeekStart(), today),
        studySessionRepository.getTestsTakenBetweenDates(getWeekStart(), today),
        studySessionRepository.getCorrectAnswersBetweenDates(getWeekStart(), today),
        studySessionRepository.getIncorrectAnswersBetweenDates(getWeekStart(), today),
        studySessionRepository.getStreak(),
        wordRepository.getWordCount(),
        reviewRepository.getMistakeCount(),
        reviewRepository.getTotalReviewCount(),
        reviewRepository.getCorrectReviewCount()
    ) { results ->
        val weekTime = results[0] as? Int ?: 0
        val weekWords = results[1] as? Int ?: 0
        val weekTests = results[2] as? Int ?: 0
        val weekCorrect = results[3] as? Int ?: 0
        val weekIncorrect = results[4] as? Int ?: 0
        val streak = results[5] as? OverallStreakEntity
        val totalWords = results[6] as? Int ?: 0
        val mistakes = results[7] as? Int ?: 0
        val totalReviews = results[8] as? Int ?: 0
        val correctReviews = results[9] as? Int ?: 0

                val quizTotal = weekCorrect + weekIncorrect
        val quizAcc = if (quizTotal > 0) (weekCorrect * 100 / quizTotal) else 0
        val reviewAcc = if (totalReviews > 0) (correctReviews * 100 / totalReviews) else 0
        val learningAcc = if (totalWords > 0) ((totalWords - mistakes) * 100 / totalWords) else 0
        val weightedAccuracy = (quizAcc * 30 + reviewAcc * 20 + learningAcc * 50) / 100

        StatisticsUiState(
            weekStudyTime = weekTime,
            weekWords = weekWords,
            weekTests = weekTests,
            weekCorrect = weekCorrect,
            weekIncorrect = weekIncorrect,
            accuracy = weightedAccuracy,
            totalWords = totalWords,
            mistakeCount = mistakes,
            currentStreak = streak?.currentStreak ?: 0,
            bestStreak = streak?.bestStreak ?: 0,
            activeDays = streak?.totalActiveDays ?: 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    private fun getWeekStart(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return dateFormat.format(calendar.time)
    }
}
