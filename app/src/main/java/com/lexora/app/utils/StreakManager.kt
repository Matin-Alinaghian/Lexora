package com.lexora.app.utils

import android.content.Context
import com.lexora.app.data.repository.StreakRepository
import com.lexora.app.data.repository.StudySessionRepository
import com.lexora.app.data.local.entity.StudySessionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val streakRepository: StreakRepository,
    private val studySessionRepository: StudySessionRepository
) {
    
    suspend fun registerStudySession(
        studyTimeMinutes: Int = 0,
        wordsReviewed: Int = 0,
        quizzesCompleted: Int = 0,
        correctAnswers: Int = 0,
        wrongAnswers: Int = 0
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        studySessionRepository.recordSession(
            StudySessionEntity(
                date = today,
                studyTimeMinutes = studyTimeMinutes,
                wordsStudied = wordsReviewed,
                testsTaken = quizzesCompleted,
                correctAnswers = correctAnswers,
                incorrectAnswers = wrongAnswers
            )
        )
                streakRepository.addStudySession(studyTimeMinutes, wordsReviewed, quizzesCompleted, correctAnswers, wrongAnswers)
    }

    
    suspend fun recordActivity() {
        registerStudySession(studyTimeMinutes = 0)
    }

    
    fun getCurrentStreak(): Flow<Int> = streakRepository.getCurrentStreak()

    
    suspend fun getBestStreak(): Int = streakRepository.getLongestStreak()

    
    fun getTodayWordsReviewed(): Flow<Int> = streakRepository.getTodayWordsReviewed()

    
    fun getTodayStudyTime(): Flow<Int> = streakRepository.getTodayStudyTime()

    
    fun getTodayQuizzes(): Flow<Int> = streakRepository.getTodayQuizzes()
}
