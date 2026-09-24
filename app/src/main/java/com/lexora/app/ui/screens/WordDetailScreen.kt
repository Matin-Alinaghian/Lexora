package com.lexora.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.ui.components.*
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    navController: NavController,
    viewModel: WordDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            show = true,
            onConfirm = {
                viewModel.deleteWord()
                navController.popBackStack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    uiState.word?.let { word ->
                var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }
        val alpha by animateFloatAsState(if (visible) 1f else 0f, animationSpec = tween(400), label = "alpha")
        val offsetY by animateFloatAsState(if (visible) 0f else 30f, animationSpec = tween(400, easing = FastOutSlowInEasing), label = "offsetY")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { this.alpha = alpha; translationY = offsetY }
        ) {
                        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text(word.englishWord, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.toggleLeitner() }) {
                    Icon(
                        if (word.isInLeitner) Icons.Filled.FlashOn else Icons.Outlined.FlashOn,
                        contentDescription = "Leitner",
                        tint = if (word.isInLeitner) SunsetGradientStart else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        if (word.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (word.isFavorite) Gold else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = {
                    navController.navigate(Screen.EditWord.createRoute(word.id))
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = PrimaryBlue)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Error)
                }
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = word.englishWord,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (word.pronunciation.isNotBlank()) {
                                Text(
                                    text = word.pronunciation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = {
                            soundManager.playSound(SoundManager.SoundType.CLICK)
                            viewModel.speakText(word.englishWord)
                        }) {
                            Icon(
                                Icons.Filled.VolumeUp,
                                contentDescription = "Pronounce",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                                if (word.persianMeaning.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Meaning", style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(word.persianMeaning, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.wordType.isNotBlank() || word.category.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (word.wordType.isNotBlank()) {
                            GlassCard(modifier = Modifier.weight(1f)) {
                                Text("Type", style = MaterialTheme.typography.titleMedium, color = Cyan)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(word.wordType, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                        if (word.category.isNotBlank()) {
                            GlassCard(modifier = Modifier.weight(1f)) {
                                Text("Category", style = MaterialTheme.typography.titleMedium, color = PurpleAccent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(word.category, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.example.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Example", style = MaterialTheme.typography.titleMedium, color = Mint)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(word.example, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                        if (word.exampleTranslation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(word.exampleTranslation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            viewModel.speakText(word.example)
                        }) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Pronounce example", tint = Mint)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.synonyms.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Synonyms", style = MaterialTheme.typography.titleMedium, color = Success)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            word.synonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { synonym ->
                                CategoryChip(text = synonym, color = Success)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.antonyms.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Antonyms", style = MaterialTheme.typography.titleMedium, color = Coral)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            word.antonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { antonym ->
                                CategoryChip(text = antonym, color = Coral)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.wordFamily.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Word Family", style = MaterialTheme.typography.titleMedium, color = WarmOrange)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            word.wordFamily.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { family ->
                                CategoryChip(text = family, color = WarmOrange)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.level.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Level", style = MaterialTheme.typography.titleMedium, color = LightPurple)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(word.level, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.isInLeitner) {
                    val boxColors = listOf(
                        Color(0xFFE91E63),                         Color(0xFFFF9800),                         Color(0xFFFFEB3B),                         Color(0xFF4CAF50),                         Color(0xFF2196F3),                     )
                    val boxColor = boxColors.getOrElse(word.leitnerBox - 1) { Color.Gray }
                    
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = SunsetGradientStart)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Leitner Review", style = MaterialTheme.typography.titleMedium, color = SunsetGradientStart)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = boxColor.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "B${word.leitnerBox}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = boxColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Currently in Box ${word.leitnerBox}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.personalNote.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Personal Note", style = MaterialTheme.typography.titleMedium, color = Gold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(word.personalNote, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (word.tags.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Tags", style = MaterialTheme.typography.titleMedium, color = LightCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            word.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                                CategoryChip(text = tag, color = LightCyan)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    } ?: LoadingState()
}
