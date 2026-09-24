package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val bulletPoints: String = "",     val category: String = "",
    val isFavorite: Boolean = false,
    val isInLeitner: Boolean = false,
    val leitnerBox: Int = 0,
    val nextReviewDate: Long = 0,
    val xpEarned: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
