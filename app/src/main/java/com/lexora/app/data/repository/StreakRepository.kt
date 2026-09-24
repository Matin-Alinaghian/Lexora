package com.lexora.app.data.repository

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.lexora.app.data.local.dao.StreakDao
import com.lexora.app.data.local.entity.DailyStreakEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val streakDao: StreakDao
) {
    
    suspend fun addStudySession(
        studyTimeMinutes: Int = 0,
        wordsReviewed: Int = 0,
        quizzesCompleted: Int = 0,
        correctAnswers: Int = 0,
        wrongAnswers: Int = 0
    ) {
        val today = getTodayStartMillis()
        
                val existingStreak = streakDao.getStreakForDate(today)
        
        if (existingStreak != null) {
                        val updated = existingStreak.copy(
                studyTimeMinutes = existingStreak.studyTimeMinutes + studyTimeMinutes,
                wordsReviewed = existingStreak.wordsReviewed + wordsReviewed,
                quizzesCompleted = existingStreak.quizzesCompleted + quizzesCompleted,
                correctAnswers = existingStreak.correctAnswers + correctAnswers,
                wrongAnswers = existingStreak.wrongAnswers + wrongAnswers
            )
            streakDao.update(updated)
        } else {
                        val newStreak = DailyStreakEntity(
                date = today,
                studyTimeMinutes = studyTimeMinutes,
                wordsReviewed = wordsReviewed,
                quizzesCompleted = quizzesCompleted,
                correctAnswers = correctAnswers,
                wrongAnswers = wrongAnswers,
                isActive = true
            )
            streakDao.insert(newStreak)
        }
        
                updateYesterdayStreakStatus()
    }

    private fun updateYesterdayStreakStatus() {
        val yesterday = getYesterdayStartMillis()
        val today = getTodayStartMillis()
        
                    }

    
    fun getCurrentStreak(): Flow<Int> = streakDao.getAllStreaks().map { streaks ->
        calculateCurrentStreak(streaks)
    }

    private fun calculateCurrentStreak(streaks: List<DailyStreakEntity>): Int {
        if (streaks.isEmpty()) return 0
        
        val today = getTodayStartMillis()
        val todayStreak = streaks.find { it.date == today }
        
        if (todayStreak == null) return 0
        
        var streakLength = 1
        var currentDate = today
        
        while (currentDate > 0) {
            val previousDate = currentDate - (24 * 60 * 60 * 1000L)
            
            if (streaks.any { it.date == previousDate && it.isActive }) {
                streakLength++
                currentDate = previousDate
            } else {
                break
            }
        }
        
        return streakLength
    }

    
    fun getTodayWordsReviewed(): Flow<Int> = streakDao
        .getTotalWordsReviewed(getTodayStartMillis())
        .map { it ?: 0 }

    
    fun getTodayStudyTime(): Flow<Int> = streakDao
        .getTotalStudyTime(getTodayStartMillis())
        .map { it ?: 0 }

    
    fun getTodayQuizzes(): Flow<Int> = streakDao
        .getTotalQuizzesCompleted(getTodayStartMillis())
        .map { it ?: 0 }

    
    fun getTotalCorrectAnswers(): Flow<Int> = streakDao
        .getTotalCorrectAnswers()
        .map { it ?: 0 }

    
    fun getTotalWrongAnswers(): Flow<Int> = streakDao
        .getTotalWrongAnswers()
        .map { it ?: 0 }

    
    suspend fun getActiveDaysCount(): Int = streakDao.getActiveDaysCount()

    
    suspend fun getLongestStreak(): Int = streakDao.getLongestStreak() ?: 0

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getYesterdayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
