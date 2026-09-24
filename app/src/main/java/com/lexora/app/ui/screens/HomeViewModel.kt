package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.local.entity.OverallStreakEntity
import com.lexora.app.data.repository.ReviewRepository
import com.lexora.app.data.repository.StudySessionRepository
import com.lexora.app.utils.LiveStudyTimer
import com.lexora.app.utils.StreakManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val studyTimeMinutes: Int = 0,
    val wordsStudied: Int = 0,
    val testsTaken: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val accuracy: Int = 0,
    val streakPercent: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val totalXp: Int = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studySessionRepository: StudySessionRepository,
    private val reviewRepository: ReviewRepository,
    private val liveStudyTimer: LiveStudyTimer,
    private val streakManager: StreakManager
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    fun recordAppEntry() {
        viewModelScope.launch {
            streakManager.recordActivity()
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        studySessionRepository.getStudyTimeByDate(today),
        studySessionRepository.getWordsStudiedByDate(today),
        studySessionRepository.getTestsTakenByDate(today),
        studySessionRepository.getCorrectAnswersByDate(today),
        studySessionRepository.getIncorrectAnswersByDate(today),
        studySessionRepository.getStreak(),
        reviewRepository.getTotalReviewCount(),
        reviewRepository.getCorrectReviewCount(),
        reviewRepository.getMistakeCount()
    ) { results ->
        val dbTime = (results[0] as? Int) ?: 0
        val words = (results[1] as? Int) ?: 0
        val tests = (results[2] as? Int) ?: 0
        val correct = (results[3] as? Int) ?: 0
        val incorrect = (results[4] as? Int) ?: 0
        val streak = results[5] as? OverallStreakEntity
        val totalReviews = results[6] as? Int ?: 0
        val correctReviews = results[7] as? Int ?: 0
        val mistakes = results[8] as? Int ?: 0

                val quizTotal = correct + incorrect
        val quizAcc = if (quizTotal > 0) ((correct * 100) / quizTotal) else 0
        val reviewAcc = if (totalReviews > 0) (correctReviews * 100 / totalReviews) else 0
        val learningAcc = if (words > 0) ((words - mistakes) * 100 / words) else 0
        val accuracy = (quizAcc * 30 + reviewAcc * 20 + learningAcc * 50) / 100

                                        val wordPercent = ((words.coerceAtMost(10) * 100) / 10).coerceAtMost(100)
        val testPercent = ((tests.coerceAtMost(3) * 100) / 3).coerceAtMost(100)
        val timePercent = ((dbTime.coerceAtMost(15) * 100) / 15).coerceAtMost(100)
        val streakPercent = (wordPercent * 30 + testPercent * 30 + timePercent * 40) / 100

        HomeUiState(
            studyTimeMinutes = dbTime,
            wordsStudied = words,
            testsTaken = tests,
            correctAnswers = correct,
            incorrectAnswers = incorrect,
            accuracy = accuracy,
            streakPercent = streakPercent.coerceAtLeast(1),
            currentStreak = streak?.currentStreak ?: 0,
            bestStreak = streak?.bestStreak ?: 0,
            totalActiveDays = streak?.totalActiveDays ?: 0,
            totalXp = streak?.totalXp ?: 0,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
