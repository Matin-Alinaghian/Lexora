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
import androidx.compose.ui.res.stringResource
import com.lexora.app.R
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
            text = stringResource(R.string.learn_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.learn_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        LearnMenuItem(
            icon = Icons.Filled.MenuBook,
            title = stringResource(R.string.learn_vocabulary),
            subtitle = stringResource(R.string.learn_vocabulary_subtitle),
            gradient = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Vocabulary.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.School,
            title = stringResource(R.string.learn_grammar),
            subtitle = stringResource(R.string.learn_grammar_subtitle),
            gradient = Brush.linearGradient(listOf(PurpleGradientStart, PurpleGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Grammar.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.StickyNote2,
            title = stringResource(R.string.learn_notes),
            subtitle = stringResource(R.string.learn_notes_subtitle),
            gradient = Brush.linearGradient(listOf(CyanGradientStart, CyanGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Notes.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Favorite,
            title = stringResource(R.string.learn_favorites),
            subtitle = stringResource(R.string.learn_favorites_subtitle),
            gradient = Brush.linearGradient(listOf(WarmOrange, Gold)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Favorites.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Collections,
            title = stringResource(R.string.learn_quick_review),
            subtitle = stringResource(R.string.learn_quick_review_subtitle),
            gradient = Brush.linearGradient(listOf(WarmOrange, Coral)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.LeitnerReview.route + "?mode=all")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.ErrorOutline,
            title = stringResource(R.string.learn_review_mistakes),
            subtitle = stringResource(R.string.learn_review_mistakes_subtitle),
            gradient = Brush.linearGradient(listOf(MintGradientStart, MintGradientEnd)),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Mistakes.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.Filled.Quiz,
            title = stringResource(R.string.learn_quiz),
            subtitle = stringResource(R.string.learn_quiz_subtitle),
            gradient = Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
            onClick = {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Quiz.route)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LearnMenuItem(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            title = stringResource(R.string.learn_dictionary),
            subtitle = stringResource(R.string.learn_dictionary_subtitle),
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
