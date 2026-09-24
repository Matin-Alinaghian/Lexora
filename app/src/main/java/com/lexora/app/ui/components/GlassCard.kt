package com.lexora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lexora.app.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    gradient: Brush? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isLight = ThemeManager.getTheme() == ThemeType.LIGHT

    val borderColor = if (isLight) CoffeeBronze.copy(alpha = 0.3f) else GlassBorder
    val borderWidth = if (isLight) 1.5.dp else 1.dp

    val backgroundModifier = if (isLight) {
        if (gradient != null) {
            Modifier.background(gradient)
        } else {
            Modifier.background(Color.White.copy(alpha = 0.95f))
        }
    } else {
        if (gradient != null) {
            Modifier.background(gradient)
        } else {
            Modifier.background(GlassWhite)
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(backgroundModifier)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        content = content
    )
}
