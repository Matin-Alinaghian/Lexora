package com.lexora.app.ai.provider

import com.lexora.app.ai.model.*

interface AiProvider {
    
    suspend fun ask(question: AiQuestion): AiResult

    
    suspend fun enrichWord(word: String): WordAiResult

    
    suspend fun enrichGrammar(title: String): GrammarAiResult

    
    suspend fun isAvailable(): Boolean
}
