package com.lexora.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.ui.components.ConfettiEffect
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current
    var showCategoryPicker by remember { mutableStateOf(false) }

    if (showCategoryPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showCategoryPicker = false }) {
            GlassCard {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth()
                ) {
                    Text(
                        "Select Quiz Source",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.setMode("all")
                            showCategoryPicker = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("All Words (Random)") }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.setMode("leitner")
                            showCategoryPicker = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text("Due Words", color = Color.White) }

                    Spacer(modifier = Modifier.height(8.dp))

                    allCategories.forEach { category ->
                        OutlinedButton(
                            onClick = {
                                viewModel.setCategory(category)
                                showCategoryPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text(category) }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showCategoryPicker = false },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Cancel", color = Coral) }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            Text("Quiz", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
            IconButton(onClick = { showCategoryPicker = true }) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

                        LaunchedEffect(uiState.isComplete) {
            if (uiState.isComplete) {
                val accuracy = if (uiState.totalQuestions > 0) {
                    (uiState.correctCount * 100) / uiState.totalQuestions
                } else 0
                when {
                    accuracy >= 80 -> soundManager.playSound(SoundManager.SoundType.QUIZ_EXCELLENT)
                    accuracy >= 50 -> soundManager.playSound(SoundManager.SoundType.SUCCESS)
                    else -> soundManager.playSound(SoundManager.SoundType.ERROR)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Preparing questions...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                uiState.questions.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Add at least 4 words to start a quiz!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                uiState.isComplete -> {
                    QuizResult(
                        correct = uiState.correctCount,
                        incorrect = uiState.incorrectCount,
                        total = uiState.totalQuestions,
                        bestStreak = uiState.bestStreak,
                        onDone = { navController.popBackStack() },
                        onRetry = { viewModel.retry() }
                    )
                }

                else -> {
                    QuizContent(
                        uiState = uiState,
                        onOptionSelected = { index ->
                            val isCorrect = index == uiState.currentQuestion?.correctIndex
                            if (isCorrect) {
                                soundManager.playSound(SoundManager.SoundType.CARD_KNEW)
                                                                if (uiState.streak >= 2) {
                                    soundManager.playSound(SoundManager.SoundType.STREAK)
                                }
                            } else {
                                soundManager.playSound(SoundManager.SoundType.CARD_DIDNT_KNOW)
                            }
                            viewModel.selectOption(index)
                        },
                        onNext = {
                            soundManager.playSound(SoundManager.SoundType.POPUP)
                            viewModel.nextQuestion()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizContent(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit
) {
    val question = uiState.currentQuestion ?: return

        var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.currentIndex) {
        cardVisible = false
        kotlinx.coroutines.delay(50)
        cardVisible = true
    }
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "cardAlpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (cardVisible) 0f else 50f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "cardOffsetY"
    )

        LinearProgressIndicator(
        progress = { uiState.progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = PrimaryBlue,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

        Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${uiState.currentIndex + 1} / ${uiState.totalQuestions}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("✅ ${uiState.correctCount}", color = Mint, fontWeight = FontWeight.Bold)
            Text("❌ ${uiState.incorrectCount}", color = Coral, fontWeight = FontWeight.Bold)
            if (uiState.streak >= 2) {
                Text("🔥 ${uiState.streak}", color = WarmOrange, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

        GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = cardAlpha
                translationY = cardOffsetY
            }
    ) {
                val typeLabel = when (question.questionType) {
            QuestionType.EN_TO_FA -> "🌐 English → Persian"
            QuestionType.FA_TO_EN -> "🇮🇷 Persian → English"
        }
        Text(
            text = typeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

                val questionText = when (question.questionType) {
            QuestionType.EN_TO_FA -> question.word.englishWord
            QuestionType.FA_TO_EN -> question.word.persianMeaning
        }

        Text(
            text = questionText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (question.questionType == QuestionType.EN_TO_FA && question.word.pronunciation.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.word.pronunciation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

        question.options.forEachIndexed { index, option ->
        QuizOption(
            text = option,
            index = index,
            isSelected = uiState.selectedOption == index,
            isCorrect = index == question.correctIndex,
            isRevealed = uiState.isAnswerRevealed,
            delayMillis = index * 80,
            onClick = { onOptionSelected(index) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

        AnimatedVisibility(
        visible = uiState.isAnswerRevealed,
        enter = fadeIn(tween(200)) + slideInVertically(
            initialOffsetY = { 30 },
            animationSpec = spring(dampingRatio = 0.7f)
        )
    ) {
        val isCorrect = uiState.selectedOption == question.correctIndex
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCorrect) Mint else Coral
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isCorrect) "Correct! Tap for next" else "Wrong! Tap for next",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun QuizOption(
    text: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isRevealed: Boolean,
    delayMillis: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(isRevealed) {
        visible = false
        kotlinx.coroutines.delay(50)
        visible = true
    }

    val entryAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(250, delayMillis = delayMillis),
        label = "optionAlpha"
    )
    val entryOffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -30f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "optionOffsetX"
    )

        val backgroundColor = when {
        isRevealed && isCorrect -> Mint.copy(alpha = 0.2f)
        isRevealed && isSelected && !isCorrect -> Coral.copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val borderColor = when {
        isRevealed && isCorrect -> Mint
        isRevealed && isSelected && !isCorrect -> Coral
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val textColor = when {
        isRevealed && isCorrect -> Mint
        isRevealed && isSelected && !isCorrect -> Coral
        else -> MaterialTheme.colorScheme.onBackground
    }

        val scale by animateFloatAsState(
        targetValue = when {
            isRevealed && isCorrect -> 1.03f
            isRevealed && isSelected && !isCorrect -> 0.97f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "optionScale"
    )

        val shakeOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = if (isRevealed && isSelected && !isCorrect) keyframes {
            durationMillis = 400
            0f at 0; -8f at 50; 8f at 100; -6f at 150; 6f at 200; -3f at 250; 3f at 300; 0f at 400
        } else tween(0),
        label = "shakeOffset"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = entryAlpha
                translationX = entryOffsetX + shakeOffset
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(
                if (isSelected || (isRevealed && isCorrect))
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp))
                else
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(16.dp))
            )
            .clickable(enabled = !isRevealed) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
                Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(borderColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (isRevealed && isCorrect) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Correct",
                    tint = Mint,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isRevealed && isSelected && !isCorrect) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Wrong",
                    tint = Coral,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = listOf("A", "B", "C", "D").getOrElse(index) { "?" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuizResult(
    correct: Int,
    incorrect: Int,
    total: Int,
    bestStreak: Int,
    onDone: () -> Unit,
    onRetry: () -> Unit
) {
    val accuracy = if (total > 0) (correct * 100 / total) else 0

    Box(modifier = Modifier.fillMaxSize()) {
                if (accuracy >= 60) {
            ConfettiEffect(
                modifier = Modifier.fillMaxSize(),
                active = true,
                particleCount = if (accuracy >= 80) 40 else 25
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                        val scoreScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
                label = "scoreScale"
            )
            val bounce by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessLow),
                label = "bounce"
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(scoreScale * bounce)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = if (accuracy >= 70) listOf(Mint, Cyan) else listOf(Coral, WarmOrange)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$accuracy%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (bestStreak >= 3) {
                        Text(
                            text = "🔥 $bestStreak streak",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

                        var statsVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { statsVisible = true }

            val statsAlpha by animateFloatAsState(
                targetValue = if (statsVisible) 1f else 0f,
                animationSpec = tween(400, delayMillis = 200),
                label = "statsAlpha"
            )

            Column(
                modifier = Modifier.graphicsLayer { alpha = statsAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        accuracy >= 90 -> "Perfect! 🌟"
                        accuracy >= 70 -> "Well Done! 👍"
                        accuracy >= 50 -> "Good Effort! 💪"
                        else -> "Keep Practicing! 📚"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    StatColumn(value = "$correct", label = "Correct", color = Mint)
                    StatColumn(value = "$incorrect", label = "Wrong", color = Coral)
                    if (bestStreak > 0) {
                        StatColumn(value = "$bestStreak", label = "Best Streak", color = WarmOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

                        Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.displaySmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
