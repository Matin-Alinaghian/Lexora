package com.lexora.app.ai.provider

import android.content.Context
import android.content.SharedPreferences
import com.lexora.app.ai.model.AiProviderSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "lexora_ai_settings"
        private const val KEY_API_KEY = "ai_api_key"
        private const val KEY_BASE_URL = "ai_base_url"
        private const val KEY_MODEL_NAME = "ai_model_name"

                val FREE_MODELS = listOf(
            "deepseek/deepseek-r1:free" to "DeepSeek R1 (Free)",
            "deepseek/deepseek-chat:free" to "DeepSeek Chat (Free)",
            "google/gemini-2.0-flash-exp:free" to "Gemini 2.0 Flash (Free)",
            "meta-llama/llama-3.1-8b-instruct:free" to "Llama 3.1 8B (Free)",
            "qwen/qwen-2.5-7b-instruct:free" to "Qwen 2.5 7B (Free)",
            "mistralai/mistral-7b-instruct:free" to "Mistral 7B (Free)",
        )
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var providerId: String
        get() = prefs.getString("ai_provider_id", "openrouter") ?: "openrouter"
        set(value) = prefs.edit().putString("ai_provider_id", value).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value).apply()

    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, "https://openrouter.ai/api/v1/chat/completions") ?: "https://openrouter.ai/api/v1/chat/completions"
        set(value) = prefs.edit().putString(KEY_BASE_URL, value).apply()

    var modelName: String
        get() = prefs.getString(KEY_MODEL_NAME, "deepseek/deepseek-chat:free") ?: "deepseek/deepseek-chat:free"
        set(value) = prefs.edit().putString(KEY_MODEL_NAME, value).apply()

    
    var pollinationsModel: String
        get() = prefs.getString("pollinations_model", "openai") ?: "openai"
        set(value) = prefs.edit().putString("pollinations_model", value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun getCurrentSettings(): AiProviderSettings {
        return AiProviderSettings(
            providerId = "openrouter",
            apiKey = apiKey,
            baseUrl = baseUrl,
            modelName = modelName
        )
    }
}
