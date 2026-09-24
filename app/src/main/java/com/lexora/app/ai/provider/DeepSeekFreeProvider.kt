package com.lexora.app.ai.provider

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lexora.app.ai.LocalDictionary
import com.lexora.app.ai.LocalGrammar
import com.lexora.app.ai.model.*
import com.lexora.app.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepSeekFreeProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: AiSettingsManager
) : AiProvider {

    override suspend fun ask(question: AiQuestion): AiResult {
        if (!NetworkUtils.isInternetAvailable(context)) {
            android.util.Log.i("DeepSeekFree", "No internet, using local fallback")
            return localFallbackAsk(question)
        }

                try {
            val pollinationsResult = callPollinations(question.userPrompt, "en")
            if (pollinationsResult.success) return pollinationsResult
            android.util.Log.w("DeepSeekFree", "Pollinations failed: ${pollinationsResult.error}")
        } catch (e: Exception) {
            android.util.Log.e("DeepSeekFree", "Pollinations error", e)
        }

                val apiKey = settingsManager.apiKey
        if (apiKey.isNotBlank()) {
            try {
                val apiResult = callOpenRouter(question.userPrompt, "en")
                if (apiResult.success) return apiResult
                android.util.Log.w("DeepSeekFree", "OpenRouter failed: ${apiResult.error}")
            } catch (e: Exception) {
                android.util.Log.e("DeepSeekFree", "OpenRouter error", e)
            }
        }

                return localFallbackAsk(question)
    }

    override suspend fun enrichWord(word: String): WordAiResult {
                val localResult = localEnrichWord(word)

        if (!NetworkUtils.isInternetAvailable(context)) {
            android.util.Log.i("DeepSeekFree", "No internet, enriching word locally: $word")
            return localResult
        }

                try {
            val apiResult = callPollinationsForWord(word)
            if (apiResult.success && apiResult.answer != null) {
                val parsed = parseWordAiResultFromJson(apiResult.answer.text, word)
                if (parsed.error == null) {
                    android.util.Log.i("DeepSeekFree", "Word enriched via Pollinations: $word")
                    return mergeWordResults(localResult, parsed)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DeepSeekFree", "Pollinations word error", e)
        }

                val apiKey = settingsManager.apiKey
        if (apiKey.isNotBlank()) {
            try {
                val apiResult = callOpenRouterForWord(word, apiKey)
                if (apiResult.success && apiResult.answer != null) {
                    val parsed = parseWordAiResultFromJson(apiResult.answer.text, word)
                    android.util.Log.i("DeepSeekFree", "Word enriched via OpenRouter: $word")
                    return mergeWordResults(localResult, parsed)
                } else {
                    android.util.Log.w("DeepSeekFree", "Word API failed: ${apiResult.error}")
                }
            } catch (e: Exception) {
                android.util.Log.e("DeepSeekFree", "Word API error", e)
            }
        }
        return localResult
    }

    override suspend fun enrichGrammar(title: String): GrammarAiResult {
        val localResult = localEnrichGrammar(title)

        if (!NetworkUtils.isInternetAvailable(context)) {
            android.util.Log.i("DeepSeekFree", "No internet, enriching grammar locally: $title")
            return localResult
        }

                try {
            val apiResult = callPollinationsForGrammar(title)
            if (apiResult.success && apiResult.answer != null) {
                val parsed = parseGrammarAiResultFromJson(apiResult.answer.text, title)
                if (parsed.error == null) {
                    android.util.Log.i("DeepSeekFree", "Grammar enriched via Pollinations: $title")
                    return mergeGrammarResults(localResult, parsed)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DeepSeekFree", "Pollinations grammar error", e)
        }

                val apiKey = settingsManager.apiKey
        if (apiKey.isNotBlank()) {
            try {
                val apiResult = callOpenRouterForGrammar(title, apiKey)
                if (apiResult.success && apiResult.answer != null) {
                    val parsed = parseGrammarAiResultFromJson(apiResult.answer.text, title)
                    android.util.Log.i("DeepSeekFree", "Grammar enriched via OpenRouter: $title")
                    return mergeGrammarResults(localResult, parsed)
                } else {
                    android.util.Log.w("DeepSeekFree", "Grammar API failed: ${apiResult.error}")
                }
            } catch (e: Exception) {
                android.util.Log.e("DeepSeekFree", "Grammar API error", e)
            }
        }
        return localResult
    }

    override suspend fun isAvailable(): Boolean = true 
    
    
    private suspend fun callPollinations(prompt: String, languageHint: String): AiResult {
                val models = listOf(settingsManager.pollinationsModel, "openai", "gpt-4o-mini", "mistral")
        
        var lastError = ""
        for (model in models.filter { it.isNotBlank() }.distinct()) {
            val result = executePollinationsRequest(prompt, model, languageHint)
            if (result.success) return result
            lastError = result.error ?: "Unknown"
        }
        
        return AiResult(success = false, error = lastError)
    }

    private suspend fun executePollinationsRequest(prompt: String, model: String, languageHint: String): AiResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://text.pollinations.ai/openai")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 45000

                val jsonBody = JsonObject().apply {
                    addProperty("model", model)
                    add("messages", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("role", "system")
                            addProperty("content", "You are an AI assistant helping a user learn English. Provide clear, accurate information.")
                        })
                        add(JsonObject().apply {
                            addProperty("role", "user")
                            addProperty("content", prompt)
                        })
                    })
                    addProperty("temperature", 0.5)
                }

                connection.outputStream.use { os ->
                    os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val content = try {
                        JsonParser.parseString(responseText).asJsonObject
                            .getAsJsonArray("choices")?.get(0)?.asJsonObject
                            ?.getAsJsonObject("message")?.get("content")?.asString
                    } catch (_: Exception) { 
                        responseText 
                    }

                    if (!content.isNullOrBlank() &&
                        !content.contains("budget", ignoreCase = true) &&
                        !content.contains("rate limit", ignoreCase = true)
                    ) {
                        AiResult(success = true, answer = AiAnswer(text = content, languageTag = languageHint))
                    } else {
                        AiResult(success = false, error = "Model $model is currently unavailable.")
                    }
                } else {
                    AiResult(success = false, error = "Server error ($responseCode) for $model")
                }
            } catch (e: Exception) {
                AiResult(success = false, error = "Connection error: ${e.message}")
            }
        }
    }

    private suspend fun callPollinationsForWord(word: String): AiResult {
        val prompt = """You are a professional English tutor. Enrich the word "$word".
Return ONLY a JSON object (no markdown, no code blocks):
{
  "pronunciation": "/phonetic/",
  "wordType": "noun/verb/adjective/etc",
  "level": "A1/A2/B1/B2/C1/C2",
  "synonyms": ["word1", "word2", "word3"],
  "antonyms": ["word1", "word2"],
  "wordFamily": ["base", "derived1"],
  "example": "An example sentence in English.",
  "exampleTranslation": "ترجمه فارسی جمله مثال",
  "tags": ["tag1"]
}"""
        return callPollinations(prompt, "en")
    }

    private suspend fun callPollinationsForGrammar(title: String): AiResult {
        val prompt = """You are a professional English grammar tutor. Explain "$title".
Return ONLY a JSON object (no markdown, no code blocks):
{
  "explanation": "Clear explanation in English.",
  "positiveForm": "Positive form example.",
  "negativeForm": "Negative form example.",
  "questionForm": "Question form example.",
  "examples": [{"en": "English example.", "fa": "ترجمه فارسی"}],
  "tips": "Helpful tips.",
  "category": "e.g., Tenses"
}"""
        return callPollinations(prompt, "en")
    }

    
    private suspend fun callOpenRouter(prompt: String, languageHint: String): AiResult {
        val apiKey = settingsManager.apiKey
        if (apiKey.isBlank()) {
            return AiResult(success = false, error = "No API key configured")
        }

        return withContext(Dispatchers.IO) {
            try {
                val baseUrl = settingsManager.baseUrl.ifBlank {
                    "https://openrouter.ai/api/v1/chat/completions"
                }
                val model = settingsManager.modelName.ifBlank {
                    "deepseek/deepseek-chat:free"
                }

                val url = URL(baseUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $apiKey")
                connection.setRequestProperty("HTTP-Referer", "https://lexora-app.com")
                connection.setRequestProperty("X-Title", "Lexora English Tutor")
                connection.doOutput = true
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                val jsonBody = JsonObject().apply {
                    addProperty("model", model)
                    add("messages", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("role", "system")
                            addProperty("content", "You are a professional English tutor for Persian speakers. Always return valid JSON without markdown code blocks.")
                        })
                        add(JsonObject().apply {
                            addProperty("role", "user")
                            addProperty("content", prompt)
                        })
                    })
                    addProperty("temperature", 0.3)
                    addProperty("max_tokens", 1000)
                }

                connection.outputStream.use { os ->
                    os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                android.util.Log.d("DeepSeekFree", "API response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    android.util.Log.d("DeepSeekFree", "API response: ${responseText.take(200)}")
                    val jsonResponse = JsonParser.parseString(responseText).asJsonObject
                    val content = jsonResponse.getAsJsonArray("choices")
                        ?.get(0)?.asJsonObject
                        ?.getAsJsonObject("message")
                        ?.get("content")?.asString

                    if (!content.isNullOrBlank()) {
                        AiResult(success = true, answer = AiAnswer(text = content, languageTag = languageHint))
                    } else {
                        AiResult(success = false, error = "Empty response from AI")
                    }
                } else {
                    val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown"
                    android.util.Log.e("DeepSeekFree", "API error body: $errorText")
                    AiResult(success = false, error = "API error ($responseCode)")
                }
            } catch (e: Exception) {
                AiResult(success = false, error = "Network error: ${e.message}")
            }
        }
    }

    private suspend fun callOpenRouterForWord(word: String, apiKey: String): AiResult {
        val prompt = """You are a professional English tutor. Enrich the word "$word".
Return ONLY a JSON object (no markdown, no code blocks):
{
  "pronunciation": "/phonetic/",
  "wordType": "noun/verb/adjective/etc",
  "level": "A1/A2/B1/B2/C1/C2",
  "synonyms": ["word1", "word2", "word3"],
  "antonyms": ["word1", "word2"],
  "wordFamily": ["base", "derived1", "derived2"],
  "example": "An example sentence in English.",
  "exampleTranslation": "ترجمه فارسی جمله مثال",
  "tags": ["tag1", "tag2"]
}"""
        return callOpenRouter(prompt, "en")
    }

    private suspend fun callOpenRouterForGrammar(title: String, apiKey: String): AiResult {
        val prompt = """You are a professional English grammar tutor. Explain "$title".
Return ONLY a JSON object (no markdown, no code blocks):
{
  "explanation": "Clear explanation in English.",
  "positiveForm": "Positive form example.",
  "negativeForm": "Negative form example.",
  "questionForm": "Question form example.",
  "examples": [{"en": "English example.", "fa": "ترجمه فارسی"}],
  "tips": "Helpful tips.",
  "category": "e.g., Tenses"
}"""
        return callOpenRouter(prompt, "en")
    }

    
    private fun localFallbackAsk(question: AiQuestion): AiResult {
        val prompt = question.userPrompt.lowercase()
        val answer = when {
            prompt.contains("since") && prompt.contains("for") -> """
                |'Since' vs 'For':
                |• Since = specific point in time (since Monday, since 2010)
                |• For = duration (for two weeks, for three years)
                |Example: I have lived here since 2015. (for 9 years)
            """.trimMargin()
            prompt.contains("hello") || prompt.contains("سلام") -> 
                "Hello! I am Lexora's AI tutor. I can help you with English grammar, vocabulary, and examples. (سلام! من دستیار هوش مصنوعی لکسورا هستم. من می‌توانم در یادگیری گرامر و واژگان انگلیسی به شما کمک کنم.)"
            prompt.contains("help") || prompt.contains("کمک") ->
                "You can ask me things like: 'What is the difference between since and for?' or 'Give me examples for Present Perfect'. To get better answers, please add an API key in Settings."
            else ->
                "I'm working in offline mode. For smarter and more detailed answers, please add an OpenRouter API key in the AI Settings screen. (در حال حاضر در حالت آفلاین هستم. برای پاسخ‌های هوشمندتر، لطفاً کلید API را در تنظیمات وارد کنید.)"
        }
        return AiResult(success = true, answer = AiAnswer(text = answer, languageTag = "en"))
    }

    private fun localEnrichWord(word: String): WordAiResult {
        val lower = word.lowercase()
        return try {
            val dictEntry = LocalDictionary.getWord(lower) ?: run {
                return WordAiResult(
                    word = word,
                    pronunciation = phoneticGuess(word),
                    wordType = guessWordType(word),
                    level = "Unknown",
                    synonyms = guessSynonyms(word),
                    antonyms = guessAntonyms(word),
                    wordFamily = wordFamilyGuess(word),
                    example = "${word.replaceFirstChar { it.uppercase() }} is a word you can learn.",
                    usedProvider = "local-fallback"
                )
            }
            WordAiResult(
                word = word,
                pronunciation = dictEntry.pronunciation,
                wordType = dictEntry.wordType,
                level = dictEntry.level,
                synonyms = dictEntry.synonyms,
                antonyms = dictEntry.antonyms,
                wordFamily = dictEntry.wordFamily,
                example = dictEntry.example,
                exampleTranslation = dictEntry.exampleTranslation,
                tags = dictEntry.tags,
                usedProvider = "local-fallback"
            )
        } catch (e: Exception) {
            WordAiResult(word = word, error = "Local error: ${e.message}", usedProvider = "local-fallback")
        }
    }

    private fun localEnrichGrammar(title: String): GrammarAiResult {
        val lower = title.lowercase()
        return try {
            val entry = LocalGrammar.getGrammar(lower) ?: run {
                return GrammarAiResult(
                    title = title,
                    explanation = "A grammar topic. Add explanation, forms and examples.",
                    usedProvider = "local-fallback"
                )
            }
            GrammarAiResult(
                title = title,
                explanation = entry.explanation,
                positiveForm = entry.positiveForm,
                negativeForm = entry.negativeForm,
                questionForm = entry.questionForm,
                examples = entry.examples,
                tips = entry.tips,
                category = entry.category,
                usedProvider = "local-fallback"
            )
        } catch (e: Exception) {
            GrammarAiResult(title = title, error = "Local error: ${e.message}", usedProvider = "local-fallback")
        }
    }

    
    private fun mergeWordResults(local: WordAiResult, api: WordAiResult): WordAiResult {
        return WordAiResult(
            word = local.word,
            pronunciation = api.pronunciation.takeIf { it.isNotBlank() } ?: local.pronunciation,
            wordType = api.wordType.takeIf { it.isNotBlank() } ?: local.wordType,
            level = api.level.takeIf { it.isNotBlank() } ?: local.level,
            synonyms = api.synonyms.takeIf { it.isNotEmpty() } ?: local.synonyms,
            antonyms = api.antonyms.takeIf { it.isNotEmpty() } ?: local.antonyms,
            wordFamily = api.wordFamily.takeIf { it.isNotEmpty() } ?: local.wordFamily,
            example = api.example.takeIf { it.isNotBlank() } ?: local.example,
            exampleTranslation = api.exampleTranslation.takeIf { it.isNotBlank() } ?: local.exampleTranslation,
            tags = api.tags.takeIf { it.isNotEmpty() } ?: local.tags,
            usedProvider = api.usedProvider.takeIf { it.isNotBlank() } ?: local.usedProvider
        )
    }

    private fun mergeGrammarResults(local: GrammarAiResult, api: GrammarAiResult): GrammarAiResult {
        return GrammarAiResult(
            title = local.title,
            explanation = api.explanation.takeIf { it.isNotBlank() } ?: local.explanation,
            positiveForm = api.positiveForm.takeIf { it.isNotBlank() } ?: local.positiveForm,
            negativeForm = api.negativeForm.takeIf { it.isNotBlank() } ?: local.negativeForm,
            questionForm = api.questionForm.takeIf { it.isNotBlank() } ?: local.questionForm,
            examples = api.examples.takeIf { it.isNotEmpty() } ?: local.examples,
            tips = api.tips.takeIf { it.isNotBlank() } ?: local.tips,
            category = api.category.takeIf { it.isNotBlank() } ?: local.category,
            usedProvider = api.usedProvider.takeIf { it.isNotBlank() } ?: local.usedProvider
        )
    }

    
    private fun parseWordAiResultFromJson(jsonText: String, fallbackWord: String): WordAiResult {
        return try {
            val clean = cleanJson(jsonText)
            val json = JsonParser.parseString(clean).asJsonObject
            WordAiResult(
                word = fallbackWord,
                pronunciation = json.get("pronunciation")?.asString ?: "",
                wordType = json.get("wordType")?.asString ?: "",
                level = json.get("level")?.asString ?: "",
                synonyms = json.getAsJsonArray("synonyms")?.map { it.asString } ?: emptyList(),
                antonyms = json.getAsJsonArray("antonyms")?.map { it.asString } ?: emptyList(),
                wordFamily = json.getAsJsonArray("wordFamily")?.map { it.asString } ?: emptyList(),
                example = json.get("example")?.asString ?: "",
                exampleTranslation = json.get("exampleTranslation")?.asString ?: "",
                tags = json.getAsJsonArray("tags")?.map { it.asString } ?: emptyList(),
                usedProvider = "openrouter-api"
            )
        } catch (e: Exception) {
            WordAiResult(word = fallbackWord, error = "Parse error", usedProvider = "openrouter-api")
        }
    }

    private fun parseGrammarAiResultFromJson(jsonText: String, fallbackTitle: String): GrammarAiResult {
        return try {
            val clean = cleanJson(jsonText)
            val json = JsonParser.parseString(clean).asJsonObject
            val examplesArray = json.getAsJsonArray("examples") ?: JsonArray()
            val examplesList = examplesArray.mapNotNull {
                try {
                    val obj = it.asJsonObject
                    GrammarExample(
                        en = obj.get("en")?.asString ?: "",
                        fa = obj.get("fa")?.asString ?: ""
                    )
                } catch (_: Exception) { null }
            }
            GrammarAiResult(
                title = fallbackTitle,
                explanation = json.get("explanation")?.asString ?: "",
                positiveForm = json.get("positiveForm")?.asString ?: "",
                negativeForm = json.get("negativeForm")?.asString ?: "",
                questionForm = json.get("questionForm")?.asString ?: "",
                examples = examplesList,
                tips = json.get("tips")?.asString ?: "",
                category = json.get("category")?.asString ?: "",
                usedProvider = "openrouter-api"
            )
        } catch (e: Exception) {
            GrammarAiResult(title = fallbackTitle, error = "Parse error", usedProvider = "openrouter-api")
        }
    }

    private fun cleanJson(text: String): String {
        var clean = text.trim()
        
                if (clean.contains("```")) {
            val pattern = "```(?:json)?(.*?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = pattern.find(clean)
            if (match != null) {
                clean = match.groupValues[1].trim()
            }
        }
        
                val firstBrace = clean.indexOf('{')
        val lastBrace = clean.lastIndexOf('}')
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            clean = clean.substring(firstBrace, lastBrace + 1)
        }
        return clean
    }

    
    private fun phoneticGuess(word: String): String {
        return when {
            word.endsWith("tion") -> "/${word.removeSuffix("tion")}ʃən/"
            word.endsWith("sion") -> "/${word.removeSuffix("sion")}ʒən/"
            word.endsWith("ture") -> "/${word.removeSuffix("ture")}tʃər/"
            word.endsWith("ous") -> "/${word.removeSuffix("ous")}əs/"
            word.endsWith("ive") -> "/${word.removeSuffix("ive")}ɪv/"
            word.endsWith("ful") -> "/${word.removeSuffix("ful")}fəl/"
            word.endsWith("less") -> "/${word.removeSuffix("less")}ləs/"
            word.endsWith("ment") -> "/${word.removeSuffix("ment")}mənt/"
            word.endsWith("ly") -> "/${word.removeSuffix("ly")}li/"
            else -> ""
        }
    }

    private fun guessWordType(word: String): String {
        val lower = word.lowercase()
        return when {
            lower.endsWith("tion") || lower.endsWith("sion") || lower.endsWith("ment") ||
            lower.endsWith("ness") || lower.endsWith("ity") -> "Noun"
            lower.endsWith("ize") || lower.endsWith("ise") || lower.endsWith("ate") ||
            lower.endsWith("ify") -> "Verb"
            lower.endsWith("ful") || lower.endsWith("less") || lower.endsWith("ous") ||
            lower.endsWith("ive") || lower.endsWith("able") -> "Adjective"
            lower.endsWith("ly") -> "Adverb"
            else -> "Unknown"
        }
    }

    private fun guessSynonyms(word: String): List<String> {
        return when (word.lowercase()) {
            "happy" -> listOf("joyful", "cheerful", "glad")
            "sad" -> listOf("unhappy", "sorrowful", "gloomy")
            "big" -> listOf("large", "huge", "grand")
            "small" -> listOf("tiny", "little", "mini")
            "fast" -> listOf("quick", "rapid", "speedy")
            "slow" -> listOf("sluggish", "gradual")
            "good" -> listOf("excellent", "great", "fine")
            "bad" -> listOf("poor", "terrible", "awful")
            "beautiful" -> listOf("lovely", "pretty", "attractive")
            "smart" -> listOf("intelligent", "clever", "bright")
            "strong" -> listOf("powerful", "mighty", "sturdy")
            "weak" -> listOf("feeble", "fragile", "frail")
            else -> emptyList()
        }
    }

    private fun guessAntonyms(word: String): List<String> {
        return when (word.lowercase()) {
            "happy" -> listOf("sad", "unhappy", "gloomy")
            "sad" -> listOf("happy", "joyful", "cheerful")
            "big" -> listOf("small", "tiny", "little")
            "small" -> listOf("big", "large", "huge")
            "fast" -> listOf("slow", "sluggish")
            "slow" -> listOf("fast", "quick", "rapid")
            "good" -> listOf("bad", "poor", "terrible")
            "bad" -> listOf("good", "excellent", "great")
            "beautiful" -> listOf("ugly", "plain")
            "strong" -> listOf("weak", "feeble")
            "weak" -> listOf("strong", "powerful")
            else -> emptyList()
        }
    }

    private fun wordFamilyGuess(word: String): List<String> {
        val lower = word.lowercase()
        return when {
            lower.endsWith("tion") -> listOf(lower, "${lower.removeSuffix("tion")}tive", "${lower.removeSuffix("tion")}al")
            lower.endsWith("ment") -> listOf(lower, "${lower.removeSuffix("ment")}al")
            lower.endsWith("ness") -> listOf(lower, "${lower.removeSuffix("ness")}y", "${lower.removeSuffix("ness")}ful")
            lower.endsWith("ful") -> listOf(lower, "${lower.removeSuffix("ful")}ness", "${lower.removeSuffix("ful")}ly")
            lower.endsWith("less") -> listOf(lower, "${lower.removeSuffix("less")}ness")
            lower.endsWith("ly") -> listOf(lower, "${lower.removeSuffix("ly")}ness")
            else -> listOf(word)
        }
    }
}
