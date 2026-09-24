package com.lexora.app.ai.provider

import com.lexora.app.ai.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiServiceProvider(
    private val defaultProvider: DeepSeekFreeProvider,
    private val settingsManager: AiSettingsManager
) : AiProvider by defaultProvider {

        
    override suspend fun enrichWord(word: String): WordAiResult {
                        return defaultProvider.enrichWord(word)
    }

    override suspend fun enrichGrammar(title: String): GrammarAiResult {
        return defaultProvider.enrichGrammar(title)
    }

    override suspend fun isAvailable(): Boolean {
        return defaultProvider.isAvailable()
    }

    override suspend fun ask(question: AiQuestion): AiResult {
        return defaultProvider.ask(question)
    }
}
