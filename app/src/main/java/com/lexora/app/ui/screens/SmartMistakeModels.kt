package com.lexora.app.ui.screens

import com.lexora.app.data.local.entity.MistakeEntity

data class SmartMistakesUiState(
    val mistakes: List<MistakeEntity> = emptyList()
)

data class SmartMistakeAnalysis(
    val pattern: String = "",
    val suggestion: String = "",
    val focusArea: String = "",
    val isAnalyzing: Boolean = false,
    val commonMistakes: Map<String, Int> = emptyMap()
)
