package com.lexora.app.ui.screens

import androidx.lifecycle.ViewModel
import com.lexora.app.ai.provider.AiServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordAiViewModel @Inject constructor(
    val aiServiceProvider: AiServiceProvider
) : ViewModel()
