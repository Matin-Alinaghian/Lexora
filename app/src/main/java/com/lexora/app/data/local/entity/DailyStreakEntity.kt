package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_streaks")
data class DailyStreakEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val studyTimeMinutes: Int = 0,
    val wordsReviewed: Int = 0,
    val quizzesCompleted: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val isActive: Boolean = true
)
