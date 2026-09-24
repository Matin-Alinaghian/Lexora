package com.lexora.app.data.remote.dictionary

import com.google.gson.annotations.SerializedName

data class FreeDictResponse(
    val word: String,
    val phonetic: String?,
    val phonetics: List<FreeDictPhonetic>?,
    val meanings: List<FreeDictMeaning>?,
    val license: FreeDictLicense?,
    val sourceUrls: List<String>?
)

data class FreeDictPhonetic(
    val text: String?,
    val audio: String?
)

data class FreeDictMeaning(
    val partOfSpeech: String,
    val definitions: List<FreeDictDefinition>,
    val synonyms: List<String>?,
    val antonyms: List<String>?
)

data class FreeDictDefinition(
    val definition: String,
    val synonyms: List<String>?,
    val antonyms: List<String>?,
    val example: String?
)

data class FreeDictLicense(
    val name: String?,
    val url: String?
)

data class MyMemoryResponse(
    val responseStatus: Int? = null,
    val responseData: MyMemoryData? = null,
    val matches: List<MyMemoryMatch>? = null
)

data class MyMemoryData(
    @SerializedName("translatedText")
    val translatedText: String? = null,
    val match: Double? = null
)

data class MyMemoryMatch(
    @SerializedName("translation")
    val translation: String? = null,
    val quality: String? = null
)

data class FarsiMatrixResponse(
    val query: String?,
    val results: List<FarsiMatrixResult>?
)

data class FarsiMatrixResult(
    val word: String?,
    val translation: String?,
    val type: String?,
    val en_examples: List<String>?,
    val fa_examples: List<String>?,
    val synonyms: List<String>?,
    val antonyms: List<String>?
)

data class DictionaryResult(
    val word: String,
    val phonetic: String = "",
    val audioUrl: String = "",
    val partOfSpeech: String = "",
    val definitions: List<DefinitionItem> = emptyList(),
    val persianTranslation: String = "",
    
    val persianMeanings: List<String> = emptyList(),
    val persianExamples: List<String> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val level: String = "",     val examples: List<String> = emptyList()
)

data class DefinitionItem(
    val partOfSpeech: String,
    val definition: String,
    val example: String = ""
)
