package com.lexora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@Composable
fun LearnScreen(navController: NavController) {
    val soundManager = LocalSoundManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Learn",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Master English step by step",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        LearnMenuItem(
            icon = Icons.Filled.MenuBook,
            title = "Vocabulary",
            subtitle = "Build your word bank",
            gradient = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Vocabulary.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.School,
            title = "Grammar",
            subtitle = "Learn grammar rules",
            gradient = Brush.linearGradient(listOf(PurpleGradientStart, PurpleGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Grammar.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.StickyNote2,
            title = "Teacher Notes",
            subtitle = "Class notes & tips",
            gradient = Brush.linearGradient(listOf(CyanGradientStart, CyanGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Notes.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Favorite,
            title = "Favorites",
            subtitle = "Your starred words",
            gradient = Brush.linearGradient(listOf(WarmOrange, Gold)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Favorites.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Collections,
            title = "Quick Review",
            subtitle = "Random mix of your library",
            gradient = Brush.linearGradient(listOf(WarmOrange, Coral)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.LeitnerReview.route + "?mode=all")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.ErrorOutline,
            title = "Review Mistakes",
            subtitle = "Practice what you got wrong",
            gradient = Brush.linearGradient(listOf(MintGradientStart, MintGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Mistakes.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Quiz,
            title = "Quiz",
            subtitle = "Test your knowledge (4 options)",
            gradient = Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Quiz.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            title = "Offline Dictionary",
            subtitle = "300,000+ bilingual entries",
            gradient = Brush.linearGradient(listOf(
                Color(0xFFFFD700),
                Color(0xFFFF9800)
            )),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Dictionary.route)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LearnMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                soundManager.playSound(SoundManager.SoundType.CLICK)
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
