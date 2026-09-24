package com.lexora.app.utils

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    error("No SoundManager provided")
}
