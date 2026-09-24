package com.lexora.app.ai.model

data class WordAiResult(
    val word: String,
    val meanings: List<WordMeaning> = emptyList(),
    val pronunciation: String = "",
    val wordType: String = "",
    val level: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val wordFamily: List<String> = emptyList(),
    val example: String = "",
    val exampleTranslation: String = "",
    val tags: List<String> = emptyList(),
    val personalNote: String = "",
    val error: String? = null,
    val usedProvider: String = ""
)

data class WordMeaning(
    val text: String,
    val language: String = "fa"
)

data class GrammarAiResult(
    val title: String,
    val explanation: String = "",
    val positiveForm: String = "",
    val negativeForm: String = "",
    val questionForm: String = "",
    val examples: List<GrammarExample> = emptyList(),
    val tips: String = "",
    val category: String = "",
    val error: String? = null,
    val usedProvider: String = ""
)

data class GrammarExample(
    val en: String,
    val fa: String = ""
)

data class AiProviderSettings(
    val providerId: String = "deepseek",
    val apiKey: String = "",
    val baseUrl: String = "",
    val modelName: String = "deepseek-chat"
)

data class AiResult(
    val success: Boolean,
    val answer: AiAnswer? = null,
    val error: String? = null
)

data class AiAnswer(
    val text: String,
    val languageTag: String = "en"
)

data class AiQuestion(
    val userPrompt: String,
    val languageHint: String = "en",
    val context: Map<String, String> = emptyMap()
)
