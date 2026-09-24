package com.lexora.app.data.local.dao

import androidx.room.*
import com.lexora.app.data.local.entity.OverallStreakEntity
import com.lexora.app.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSession(session: StudySessionEntity): Long

    @Query("SELECT * FROM study_sessions WHERE date = :date")
    suspend fun getSessionByDate(date: String): StudySessionEntity?

    @Query("SELECT * FROM study_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSessionsBetweenDates(startDate: String, endDate: String): Flow<List<StudySessionEntity>>

    @Query("SELECT SUM(studyTimeMinutes) FROM study_sessions WHERE date = :date")
    fun getStudyTimeByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(studyTimeMinutes) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getStudyTimeBetweenDates(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT SUM(wordsStudied) FROM study_sessions WHERE date = :date")
    fun getWordsStudiedByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(wordsStudied) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getWordsStudiedBetweenDates(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT SUM(testsTaken) FROM study_sessions WHERE date = :date")
    fun getTestsTakenByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(testsTaken) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getTestsTakenBetweenDates(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT SUM(correctAnswers) FROM study_sessions WHERE date = :date")
    fun getCorrectAnswersByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(correctAnswers) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getCorrectAnswersBetweenDates(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT SUM(incorrectAnswers) FROM study_sessions WHERE date = :date")
    fun getIncorrectAnswersByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(incorrectAnswers) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getIncorrectAnswersBetweenDates(startDate: String, endDate: String): Flow<Int?>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: OverallStreakEntity)

    @Query("SELECT * FROM overall_streaks WHERE id = 1")
    fun getStreak(): Flow<OverallStreakEntity?>

    @Query("SELECT * FROM overall_streaks WHERE id = 1")
    suspend fun getStreakSync(): OverallStreakEntity?
}
