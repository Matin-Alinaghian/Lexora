package com.lexora.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.runtime.mutableStateOf

enum class ThemeType {
    LIGHT,
    DARK
}

object ThemeManager {
    private val _currentTheme = mutableStateOf(ThemeType.DARK)

    fun getTheme(): ThemeType = _currentTheme.value

    fun setTheme(themeType: ThemeType) {
        _currentTheme.value = themeType
    }
}

@Composable
fun LexoraThemeWithType(
    themeType: ThemeType,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeType == ThemeType.LIGHT) LightColorScheme else DarkColorScheme
    val statusBarColor = if (themeType == ThemeType.LIGHT) {
        LightSurface.toArgb()
    } else {
        DarkBackground.toArgb()
    }
    val lightStatusBars = themeType == ThemeType.LIGHT

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = statusBarColor
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = lightStatusBars
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LexoraTypography,
        content = content
    )
}
