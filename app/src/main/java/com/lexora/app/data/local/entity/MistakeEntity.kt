package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long? = null,
    val grammarId: Long? = null,
    val noteId: Long? = null,
    val question: String,
    val correctAnswer: String,
    val userAnswer: String,
    val questionType: String,
    val quizId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
