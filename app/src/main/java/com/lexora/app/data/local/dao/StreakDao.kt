package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.DailyStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM daily_streaks WHERE date = :date LIMIT 1")
    suspend fun getStreakForDate(date: Long): DailyStreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(streak: DailyStreakEntity)

    @Update
    suspend fun update(streak: DailyStreakEntity)

    @Query("SELECT * FROM daily_streaks ORDER BY date DESC")
    fun getAllStreaks(): Flow<List<DailyStreakEntity>>

    @Query("SELECT wordsReviewed FROM daily_streaks WHERE date = :date")
    fun getTotalWordsReviewed(date: Long): Flow<Int?>

    @Query("SELECT studyTimeMinutes FROM daily_streaks WHERE date = :date")
    fun getTotalStudyTime(date: Long): Flow<Int?>

    @Query("SELECT quizzesCompleted FROM daily_streaks WHERE date = :date")
    fun getTotalQuizzesCompleted(date: Long): Flow<Int?>

    @Query("SELECT SUM(correctAnswers) FROM daily_streaks")
    fun getTotalCorrectAnswers(): Flow<Int?>

    @Query("SELECT SUM(wrongAnswers) FROM daily_streaks")
    fun getTotalWrongAnswers(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM daily_streaks WHERE isActive = 1")
    suspend fun getActiveDaysCount(): Int

    @Query("SELECT COUNT(*) FROM daily_streaks")
    suspend fun getLongestStreak(): Int?
}
