package com.lexora.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

val LightBackground = CoffeeCream
val LightSurface = CoffeeSand
val LightCard = Color(0xFFFFFFFF)
val LightCardElevated = CoffeeLinen

val LightColorScheme = lightColorScheme(
    primary = CoffeeMocha,                    onPrimary = Color.White,
    primaryContainer = CoffeeCaramel.copy(alpha = 0.2f),
    onPrimaryContainer = CoffeeEspresso,
    secondary = CoffeeLatte,                   onSecondary = Color.White,
    secondaryContainer = CoffeeLatte.copy(alpha = 0.15f),
    onSecondaryContainer = CoffeeMocha,
    tertiary = CoffeeBronze,                   onTertiary = Color.White,
    tertiaryContainer = CoffeeBronze.copy(alpha = 0.15f),
    onTertiaryContainer = CoffeeMocha,
    background = LightBackground,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    surfaceVariant = LightCard,
    onSurfaceVariant = Color(0xFF1A1C1E),
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = CoffeeCaramel,
    outlineVariant = CoffeeBronze.copy(alpha = 0.5f),
    inverseSurface = Color(0xFF111111),
    inverseOnSurface = CoffeeCream,
    inversePrimary = CoffeeCaramel,
    surfaceTint = CoffeeMocha,
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = NavyBlue,
    onPrimaryContainer = LightBlue,
    secondary = Cyan,
    onSecondary = TextOnPrimary,
    secondaryContainer = DarkCard,
    onSecondaryContainer = LightCyan,
    tertiary = PurpleAccent,
    onTertiary = TextOnPrimary,
    tertiaryContainer = DeepPurple,
    onTertiaryContainer = LightPurple,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = TextOnPrimary,
    errorContainer = Color(0xFF3D1515),
    onErrorContainer = Color(0xFFFFB4AB),
    outline = TextTertiary,
    outlineVariant = GlassBorder,
    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = NavyBlue,
    surfaceTint = PrimaryBlue,
)

@Composable
fun LexoraTheme(
    content: @Composable () -> Unit
) {
    val themeType = ThemeManager.getTheme()
    val colorScheme = if (themeType == ThemeType.LIGHT) LightColorScheme else DarkColorScheme
    val isLight = themeType == ThemeType.LIGHT

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = if (isLight) LightBackground.toArgb() else DarkBackground.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = isLight
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = isLight
            }
        }
    }

    // Layout direction is pinned to RTL for both languages so that switching
    // language only changes the text, never the order/position of items.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LexoraTypography,
            content = content
        )
    }
}
