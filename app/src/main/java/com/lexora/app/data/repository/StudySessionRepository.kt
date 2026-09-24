package com.lexora.app.data.repository

import com.lexora.app.data.local.dao.StudySessionDao
import com.lexora.app.data.local.entity.OverallStreakEntity
import com.lexora.app.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudySessionRepository @Inject constructor(
    private val studySessionDao: StudySessionDao,
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getAllSessions(): Flow<List<StudySessionEntity>> = studySessionDao.getAllSessions()

    fun getSessionsBetweenDates(startDate: String, endDate: String): Flow<List<StudySessionEntity>> =
        studySessionDao.getSessionsBetweenDates(startDate, endDate)

    fun getStudyTimeByDate(date: String = getToday()): Flow<Int?> =
        studySessionDao.getStudyTimeByDate(date)

    fun getStudyTimeBetweenDates(startDate: String, endDate: String): Flow<Int?> =
        studySessionDao.getStudyTimeBetweenDates(startDate, endDate)

    fun getWordsStudiedByDate(date: String = getToday()): Flow<Int?> =
        studySessionDao.getWordsStudiedByDate(date)

    fun getWordsStudiedBetweenDates(startDate: String, endDate: String): Flow<Int?> =
        studySessionDao.getWordsStudiedBetweenDates(startDate, endDate)

    fun getTestsTakenByDate(date: String = getToday()): Flow<Int?> =
        studySessionDao.getTestsTakenByDate(date)

    fun getTestsTakenBetweenDates(startDate: String, endDate: String): Flow<Int?> =
        studySessionDao.getTestsTakenBetweenDates(startDate, endDate)

    fun getCorrectAnswersByDate(date: String = getToday()): Flow<Int?> =
        studySessionDao.getCorrectAnswersByDate(date)

    fun getCorrectAnswersBetweenDates(startDate: String, endDate: String): Flow<Int?> =
        studySessionDao.getCorrectAnswersBetweenDates(startDate, endDate)

    fun getIncorrectAnswersByDate(date: String = getToday()): Flow<Int?> =
        studySessionDao.getIncorrectAnswersByDate(date)

    fun getIncorrectAnswersBetweenDates(startDate: String, endDate: String): Flow<Int?> =
        studySessionDao.getIncorrectAnswersBetweenDates(startDate, endDate)

    suspend fun recordSession(session: StudySessionEntity) {
        val existing = studySessionDao.getSessionByDate(session.date)
        if (existing != null) {
            val updated = existing.copy(
                wordsStudied = existing.wordsStudied + session.wordsStudied,
                testsTaken = existing.testsTaken + session.testsTaken,
                correctAnswers = existing.correctAnswers + session.correctAnswers,
                incorrectAnswers = existing.incorrectAnswers + session.incorrectAnswers,
                studyTimeMinutes = existing.studyTimeMinutes + session.studyTimeMinutes
            )
            studySessionDao.insertOrUpdateSession(updated)
        } else {
            studySessionDao.insertOrUpdateSession(session)
        }
        updateStreak()
    }

    fun getStreak(): Flow<OverallStreakEntity?> = studySessionDao.getStreak()

    suspend fun updateStreak() {
        val today = getToday()
        val streak = studySessionDao.getStreakSync() ?: OverallStreakEntity()

        if (streak.lastStudyDate == today) {
                        return
        }

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val newCurrentStreak = if (streak.lastStudyDate == dateFormat.format(yesterday.time)) {
            streak.currentStreak + 1
        } else {
            1
        }

        val newBestStreak = maxOf(newCurrentStreak, streak.bestStreak)

        studySessionDao.insertOrUpdateStreak(
            streak.copy(
                currentStreak = newCurrentStreak,
                bestStreak = newBestStreak,
                totalActiveDays = streak.totalActiveDays + 1,
                lastStudyDate = today
            )
        )
    }

    suspend fun addXp(xp: Int) {
        val streak = studySessionDao.getStreakSync() ?: OverallStreakEntity()
        studySessionDao.insertOrUpdateStreak(streak.copy(totalXp = streak.totalXp + xp))
    }

    suspend fun recordStudyTime(minutes: Int) {
        if (minutes <= 0) return
        val today = getToday()
        val existing = studySessionDao.getSessionByDate(today)
        if (existing != null) {
            studySessionDao.insertOrUpdateSession(existing.copy(studyTimeMinutes = existing.studyTimeMinutes + minutes))
        } else {
            studySessionDao.insertOrUpdateSession(StudySessionEntity(date = today, studyTimeMinutes = minutes))
        }
    }

    private fun getToday(): String = dateFormat.format(Date())
}
