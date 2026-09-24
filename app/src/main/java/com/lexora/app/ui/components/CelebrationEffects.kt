package com.lexora.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.lexora.app.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    active: Boolean = true,
    particleCount: Int = 15
) {
    if (!active) return

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                size = 4f + Random.nextFloat() * 8f,
                color = listOf(Gold, Mint, Cyan, Coral, WarmOrange, PurpleAccent, LightCyan).random(),
                wobbleFreq = 1f + Random.nextFloat() * 3f,
                wobbleAmp = 10f + Random.nextFloat() * 30f,
                delay = Random.nextFloat() * 0.3f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(300.dp)) {
        val w = size.width
        val h = size.height
        particles.forEach { p ->
            val t = ((time + p.delay) % 1f)
            val y = t * h * p.speed
            val x = p.x * w + sin(t * p.wobbleFreq * Math.PI * 2).toFloat() * p.wobbleAmp
            val alpha = (1f - t).coerceIn(0f, 1f)
            val rotation = t * 720f

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.size,
                center = Offset(x, y)
            )
                        drawLine(
                color = p.color.copy(alpha = alpha * 0.6f),
                start = Offset(x - p.size, y),
                end = Offset(x + p.size, y),
                strokeWidth = 2f
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val wobbleFreq: Float,
    val wobbleAmp: Float,
    val delay: Float
)

@Composable
fun GlowRing(
    modifier: Modifier = Modifier,
    color: Color = Mint,
    active: Boolean = false
) {
    if (!active) return

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Canvas(modifier = modifier.graphicsLayer {
        scaleX = glowScale
        scaleY = glowScale
    }) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = glowAlpha), Color.Transparent),
                center = center,
                radius = size.minDimension / 2
            ),
            radius = size.minDimension / 2
        )
    }
}

@Composable
fun Modifier.scaleOnClick(
    pressed: Boolean,
    modifier: Modifier = Modifier
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "btnScale"
    )
    return this.then(modifier).graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
