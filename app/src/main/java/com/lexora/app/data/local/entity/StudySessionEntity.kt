package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,     val wordsStudied: Int = 0,
    val testsTaken: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val studyTimeMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "overall_streaks")
data class OverallStreakEntity(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val totalXp: Int = 0,
    val lastStudyDate: String = ""
)
