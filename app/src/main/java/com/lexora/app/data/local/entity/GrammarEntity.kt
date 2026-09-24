package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar")
data class GrammarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val explanation: String = "",
    val positiveForm: String = "",
    val negativeForm: String = "",
    val questionForm: String = "",
    val examples: String = "",     val tips: String = "",
    val category: String = "",
    val isFavorite: Boolean = false,
    val isInLeitner: Boolean = false,
    val leitnerBox: Int = 0,
    val nextReviewDate: Long = 0,
    val xpEarned: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
