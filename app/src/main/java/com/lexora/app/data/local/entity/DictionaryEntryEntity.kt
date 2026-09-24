package com.lexora.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dictionary_entries",
    indices = [
        Index(value = ["englishWord"], name = "idx_dict_english"),
        Index(value = ["persianWord"], name = "idx_dict_persian"),
        Index(value = ["wordType"], name = "idx_dict_source")
    ]
)
data class DictionaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val englishWord: String,
    val englishPhonetic: String = "",
    val englishDefinition: String = "",
    val englishPartOfSpeech: String = "",
    val persianWord: String = "",
    val persianPhonetic: String = "",
    val persianDefinition: String = "",
    val persianPartOfSpeech: String = "",
    val synonyms: String = "",     val antonyms: String = "",     val example: String = "",
    val exampleTranslation: String = "",
    val wordType: String = "",     val level: String = "A1",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
