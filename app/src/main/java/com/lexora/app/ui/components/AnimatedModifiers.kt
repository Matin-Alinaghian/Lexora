package com.lexora.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.shakeAnimation(
    trigger: Boolean,
    animationSpec: FiniteAnimationSpec<Float> = tween(500)
): Modifier = composed {
    val shake by animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 500
            0f at 0
            -10f at 50
            10f at 100
            -8f at 150
            8f at 200
            -5f at 250
            5f at 300
            -2f at 350
            0f at 500
        },
        label = "shake"
    )

    this.graphicsLayer {
        translationX = shake * 5f
    }
}

fun Modifier.pulseAnimation(
    trigger: Boolean
): Modifier = composed {
    val scale by animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.3f,
            stiffness = 300f
        ),
        label = "pulse"
    )

    this.graphicsLayer {
        scaleX = if (trigger) scale else 1f
        scaleY = if (trigger) scale else 1f
    }
}

fun Modifier.bounceAnimation(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                -0.1f at 100
                0f at 200
                -0.05f at 300
                0f at 400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounce"
    )

    this.graphicsLayer {
        translationY = bounce * 10f
    }
}
