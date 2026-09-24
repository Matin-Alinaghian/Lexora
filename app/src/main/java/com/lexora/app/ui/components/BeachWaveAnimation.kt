package com.lexora.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.lexora.app.ui.theme.*
import kotlin.math.sin

@Composable
fun BeachWaveAnimation(
    modifier: Modifier = Modifier,
    waveColor: Color = Cyan.copy(alpha = 0.3f),
    secondWaveColor: Color = PrimaryBlue.copy(alpha = 0.2f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    val secondWaveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondWaveOffset"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val width = size.width
        val height = size.height

        val wavePath1 = Path().apply {
            moveTo(0f, height)
            var x = -waveOffset % width
            while (x < width + 200f) {
                val y = height * 0.4f +
                        (kotlin.math.sin((x + waveOffset) * 0.015f) * height * 0.15f) +
                        (kotlin.math.sin((x + waveOffset) * 0.008f) * height * 0.1f)
                lineTo(x, y)
                x += 5f
            }
            lineTo(width, height)
            close()
        }

        drawPath(
            path = wavePath1,
            color = waveColor
        )

        val wavePath2 = Path().apply {
            moveTo(0f, height)
            var x = -secondWaveOffset % width
            while (x < width + 200f) {
                val y = height * 0.55f +
                        (kotlin.math.sin((x + secondWaveOffset) * 0.012f) * height * 0.12f) +
                        (kotlin.math.sin((x + secondWaveOffset) * 0.006f) * height * 0.08f)
                lineTo(x, y)
                x += 5f
            }
            lineTo(width, height)
            close()
        }

        drawPath(
            path = wavePath2,
            color = secondWaveColor
        )

        val foamPath = Path().apply {
            moveTo(0f, height * 0.5f)
            var x = 0f
            while (x < width) {
                val y = height * 0.45f +
                        (kotlin.math.sin((x + waveOffset * 0.8f) * 0.02f) * height * 0.05f)
                lineTo(x, y)
                x += 3f
            }
        }

        drawPath(
            path = foamPath,
            color = Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun CelebrationParticles(
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    if (!active) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60000f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val particles = remember { List(20) { ParticleData.random() } }

    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val width = size.width
        val height = size.height

        particles.forEach { particle ->
            val t = ((time + particle.delay) % particle.duration) / particle.duration.toFloat()
            val progress = t
            val xOffset = (sin(progress * Math.PI * 3) * 20f).toFloat()
            val x = particle.startX * width
            val y = height - (progress * (height + 50f))

            drawCircle(
                color = particle.color.copy(alpha = 1f - progress),
                radius = particle.size,
                center = Offset(x + xOffset, y)
            )
        }
    }
}

private data class ParticleData(
    val id: Int,
    val startX: Float,
    val size: Float,
    val color: Color,
    val duration: Int,
    val delay: Int
) {
    companion object {
        private val colors = listOf(Gold, WarmOrange, Mint, Cyan, Coral, LightCyan)
        private var counter = 0

        fun random(): ParticleData {
            return ParticleData(
                id = counter++,
                startX = Math.random().toFloat(),
                size = (3..8).random().toFloat(),
                color = colors.random(),
                duration = (1500..3000).random(),
                delay = (0..1000).random()
            )
        }
    }
}
