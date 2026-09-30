package com.lexora.app.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Persists the user's language choice and applies it to any Context.
 * Default is Persian (fa); switching to English (en) restores the original UI.
 */
object LanguageManager {
    private const val PREFS_NAME = "lexora_language"
    private const val KEY_LANGUAGE = "language"

    const val LANG_ENGLISH = "en"
    const val LANG_PERSIAN = "fa"
    const val DEFAULT_LANGUAGE = LANG_PERSIAN

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    /** Wraps [base] so that all getString() calls resolve in the saved language. */
    fun wrap(base: Context): Context {
        val locale = Locale(getLanguage(base))
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return base.createConfigurationContext(config)
    }
}
