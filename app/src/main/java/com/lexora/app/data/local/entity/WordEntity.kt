package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val englishWord: String,
    val persianMeaning: String,
    val pronunciation: String = "",
    val example: String = "",
    val exampleTranslation: String = "",
    val wordType: String = "",
    val category: String = "",
    val synonyms: String = "",     val antonyms: String = "",     val wordFamily: String = "",     val personalNote: String = "",
    val level: String = "",
    val tags: String = "",     val isFavorite: Boolean = false,
    val isInLeitner: Boolean = false,
    val leitnerBox: Int = 0,     val nextReviewDate: Long = 0,
    val xpEarned: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
