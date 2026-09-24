package com.lexora.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.data.local.entity.GrammarEntity
import com.lexora.app.data.local.entity.NoteEntity
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.ui.components.ConfettiEffect
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.CoffeeBronze
import com.lexora.app.ui.theme.CoffeeCaramel
import com.lexora.app.ui.theme.CoffeeLatte
import com.lexora.app.ui.theme.CoffeeMocha
import com.lexora.app.ui.theme.Coral
import com.lexora.app.ui.theme.Cyan
import com.lexora.app.ui.theme.DarkCard
import com.lexora.app.ui.theme.DarkSurface
import com.lexora.app.ui.theme.LightCardElevated
import com.lexora.app.ui.theme.Mint
import com.lexora.app.ui.theme.PrimaryBlue
import com.lexora.app.ui.theme.PurpleAccent
import com.lexora.app.ui.theme.ThemeManager
import com.lexora.app.ui.theme.ThemeType
import com.lexora.app.ui.theme.WarmOrange
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class LeitnerItem {
    data class Word(val entity: WordEntity) : LeitnerItem()
    data class Grammar(val entity: GrammarEntity) : LeitnerItem()
    data class Note(val entity: NoteEntity) : LeitnerItem()

    val id: Long get() = when (this) {
        is Word -> entity.id
        is Grammar -> entity.id
        is Note -> entity.id
    }
}

data class LeitnerReviewState(
    val items: List<LeitnerItem> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isComplete: Boolean = false,
    val isLoading: Boolean = true,
    val categoryName: String = ""
) {
    val currentItem: LeitnerItem? get() = items.getOrNull(currentIndex)
    val totalItems: Int get() = items.size
    val progress: Float get() = if (totalItems > 0) (currentIndex + 1).toFloat() / totalItems else 0f
}

@Composable
fun LeitnerReviewScreen(
    navController: NavController,
    viewModel: LeitnerReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current
    val isLight = ThemeManager.getTheme() == ThemeType.LIGHT

    var showResult by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            val accuracy = if (uiState.totalItems > 0) {
                (uiState.correctCount * 100) / uiState.totalItems
            } else 0
            when {
                accuracy >= 80 -> soundManager.playSound(SoundManager.SoundType.QUIZ_EXCELLENT)
                accuracy >= 50 -> soundManager.playSound(SoundManager.SoundType.SUCCESS)
                else -> soundManager.playSound(SoundManager.SoundType.ERROR)
            }
            delay(RESULT_DELAY_MS)
            showResult = true
        }
    }

    if (showCategoryPicker) {
        Dialog(onDismissRequest = { showCategoryPicker = false }) {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select a list",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Button(
                                onClick = {
                                    showCategoryPicker = false
                                    navController.navigate(Screen.LeitnerReview.route + "?mode=all")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("All words (random mix)")
                            }
                        }
                        items(allCategories) { category ->
                            OutlinedButton(
                                onClick = {
                                    showCategoryPicker = false
                                    navController.navigate(
                                        Screen.LeitnerReview.route + "?mode=category:$category"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text(
                                    text = category,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = { showCategoryPicker = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel", color = Coral)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        uiState.categoryName.isNotBlank() -> uiState.categoryName
                        viewModel.mode == "mistakes" -> "Mistakes Review"
                        else -> "Quick Review"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                if (uiState.categoryName.isNotBlank()) {
                    Text(
                        text = "List review",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (viewModel.mode == "all" || uiState.categoryName.isNotBlank()) {
                IconButton(onClick = { showCategoryPicker = true }) {
                    Icon(
                        Icons.Filled.FilterList,
                        contentDescription = "Choose list",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.items.isEmpty() -> EmptyReviewState()

            showResult -> LeitnerResult(
                correct = uiState.correctCount,
                incorrect = uiState.incorrectCount,
                total = uiState.totalItems,
                onDone = { navController.popBackStack() },
                onRetry = { viewModel.retry() }
            )

            else -> {
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.currentIndex + 1} of ${uiState.totalItems}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Knew ${uiState.correctCount}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Mint,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Missed ${uiState.incorrectCount}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Coral,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val currentItem = uiState.currentItem
                if (currentItem != null) {
                    val density = LocalDensity.current
                    val swipeDistance = with(density) { 460.dp.toPx() }
                    val enterOffset = with(density) { 14.dp.toPx() }

                    val enterProgress = remember { Animatable(1f) }
                    val exitProgress = remember { Animatable(0f) }
                    var leavingItem by remember { mutableStateOf<LeitnerItem?>(null) }
                    var leavingFlipped by remember { mutableStateOf(false) }
                    var leavingDirection by remember { mutableIntStateOf(0) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(uiState.currentIndex, uiState.items.size) {
                        enterProgress.snapTo(0f)
                        enterProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = 1f,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    val answer: (Boolean) -> Unit = { correct ->
                        if (!exitProgress.isRunning && leavingItem == null) {
                            leavingItem = currentItem
                            leavingFlipped = uiState.isFlipped
                            leavingDirection = if (correct) 1 else -1
                            soundManager.playSound(
                                if (correct) SoundManager.SoundType.CARD_KNEW
                                else SoundManager.SoundType.CARD_DIDNT_KNOW
                            )
                            scope.launch {
                                launch {
                                    delay(HANDOFF_DELAY_MS)
                                    enterProgress.snapTo(0f)
                                    if (correct) viewModel.markCorrect() else viewModel.markIncorrect()
                                }
                                exitProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = 320,
                                        easing = FastOutLinearInEasing
                                    )
                                )
                                leavingItem = null
                                leavingDirection = 0
                                exitProgress.snapTo(0f)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(CARD_HEIGHT_FRACTION)
                        ) {
                            val leaving = leavingItem
                            val isLeavingCurrent = leaving != null &&
                                leaving::class == currentItem::class &&
                                leaving.id == currentItem.id
                            val enter = enterProgress.value

                            FlipCard(
                                item = currentItem,
                                isFlipped = uiState.isFlipped,
                                isLight = isLight,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        translationY = (1f - enter) * enterOffset
                                        rotationZ = -(1f - enter) * 1.4f
                                        val settle = 0.94f + 0.06f * enter
                                        scaleX = settle
                                        scaleY = settle
                                        alpha = if (isLeavingCurrent) {
                                            0f
                                        } else {
                                            (enter * 2.4f).coerceAtMost(1f)
                                        }
                                    },
                                onFlip = {
                                    soundManager.playSound(SoundManager.SoundType.CARD_REVEAL)
                                    viewModel.flipCard()
                                },
                                onSpeak = {
                                    soundManager.playSound(SoundManager.SoundType.CLICK)
                                    viewModel.speak(
                                        when (currentItem) {
                                            is LeitnerItem.Word -> currentItem.entity.englishWord
                                            is LeitnerItem.Grammar -> currentItem.entity.title
                                            is LeitnerItem.Note -> currentItem.entity.title
                                        }
                                    )
                                }
                            )

                            if (leaving != null) {
                                val exit = exitProgress.value
                                FlipCard(
                                    item = leaving,
                                    isFlipped = leavingFlipped,
                                    isLight = isLight,
                                    answerTint = if (leavingDirection > 0) Mint else Coral,
                                    answerProgress = exit,
                                    interactive = false,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            translationX = leavingDirection * exit * swipeDistance
                                            translationY = -20f * exit
                                            rotationZ = leavingDirection * 13f * exit
                                            val fly = 1f - 0.06f * exit
                                            scaleX = fly
                                            scaleY = fly
                                            alpha = 1f - 0.85f * exit
                                        },
                                    onFlip = {},
                                    onSpeak = {}
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnswerButton(
                                label = "Missed it",
                                icon = Icons.Filled.Close,
                                tint = Coral,
                                isLight = isLight,
                                modifier = Modifier.weight(1f),
                                onClick = { answer(false) }
                            )
                            AnswerButton(
                                label = "Knew it",
                                icon = Icons.Filled.Check,
                                tint = Mint,
                                isLight = isLight,
                                modifier = Modifier.weight(1f),
                                onClick = { answer(true) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    isLight: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessHigh),
        label = "pressScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale },
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = tint.copy(alpha = if (isLight) 0.14f else 0.2f),
            contentColor = tint
        ),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FlipCard(
    item: LeitnerItem,
    isFlipped: Boolean,
    isLight: Boolean,
    modifier: Modifier = Modifier,
    answerTint: Color? = null,
    answerProgress: Float = 0f,
    interactive: Boolean = true,
    onFlip: () -> Unit,
    onSpeak: () -> Unit = {}
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "flip"
    )

    val lift by animateFloatAsState(
        targetValue = if (isFlipped) 1.015f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "lift"
    )

    val shape = RoundedCornerShape(28.dp)

    val accentColor = when (item) {
        is LeitnerItem.Word -> if (isLight) CoffeeMocha else PrimaryBlue
        is LeitnerItem.Grammar -> if (isLight) CoffeeLatte else PurpleAccent
        is LeitnerItem.Note -> if (isLight) CoffeeBronze else Cyan
    }
    val typeLabel = when (item) {
        is LeitnerItem.Word -> "WORD"
        is LeitnerItem.Grammar -> "GRAMMAR"
        is LeitnerItem.Note -> "NOTE"
    }
    val backLabel = when (item) {
        is LeitnerItem.Word -> "MEANING"
        is LeitnerItem.Grammar -> "EXPLANATION"
        is LeitnerItem.Note -> "CONTENT"
    }
    val typeIcon = when (item) {
        is LeitnerItem.Word -> Icons.AutoMirrored.Outlined.MenuBook
        is LeitnerItem.Grammar -> Icons.Filled.School
        is LeitnerItem.Note -> Icons.AutoMirrored.Filled.StickyNote2
    }

    val cardGradient = if (isLight) {
        Brush.verticalGradient(listOf(Color.White, LightCardElevated))
    } else {
        Brush.verticalGradient(listOf(DarkCard, DarkSurface))
    }
    val borderColor = if (isLight) accentColor.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.10f)

    BoxWithConstraints(
        modifier = modifier
            .shadow(
                elevation = if (isLight) 16.dp else 10.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.55f),
                spotColor = accentColor.copy(alpha = 0.55f)
            )
            .clip(shape)
            .background(cardGradient)
            .border(1.dp, borderColor, shape)
            .clickable(enabled = interactive) { onFlip() }
            .graphicsLayer {
                scaleX = lift
                scaleY = lift
                rotationY = rotation
                cameraDistance = 18f * density
            },
        contentAlignment = Alignment.Center
    ) {
        val cardHeight = maxHeight.value
        val frontMax = (cardHeight * 0.085f).coerceIn(20f, 30f).sp
        val frontMin = (frontMax.value * 0.55f).sp
        val backMax = (cardHeight * 0.05f).coerceIn(13f, 20f).sp
        val backMin = (backMax.value * 0.6f).sp

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.9f), accentColor.copy(alpha = 0.05f))
                    )
                )
        )

        if (rotation <= 90f) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = accentColor
                        )
                    }

                    IconButton(
                        onClick = { onSpeak() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                AutoFitText(
                    text = when (item) {
                        is LeitnerItem.Word -> item.entity.englishWord
                        is LeitnerItem.Grammar -> item.entity.title
                        is LeitnerItem.Note -> item.entity.title
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    maxFontSize = frontMax,
                    minFontSize = frontMin,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Text(
                    text = "Tap to reveal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    letterSpacing = 0.8.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .graphicsLayer { rotationY = 180f },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = backLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = accentColor.copy(alpha = 0.9f)
                )

                AutoFitText(
                    text = when (item) {
                        is LeitnerItem.Word -> item.entity.persianMeaning
                        is LeitnerItem.Grammar -> item.entity.explanation
                        is LeitnerItem.Note -> item.entity.content
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    maxFontSize = backMax,
                    minFontSize = backMin,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Text(
                    text = "Tap to flip back",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    letterSpacing = 0.8.sp
                )
            }
        }

        if (answerTint != null && answerProgress > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(answerTint.copy(alpha = 0.22f * answerProgress))
            )
        }
    }
}

@Composable
private fun AutoFitText(
    text: String,
    color: Color,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val measurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val availableWidth = with(density) { maxWidth.toPx() }.toInt()
        val availableHeight = with(density) { maxHeight.toPx() }.toInt()

        val fontSize = remember(text, availableWidth, availableHeight, maxFontSize, minFontSize, fontWeight) {
            if (availableWidth <= 0 || availableHeight <= 0 || text.isBlank()) {
                maxFontSize
            } else {
                val constraints = Constraints(maxWidth = availableWidth)
                var low = minFontSize.value
                var high = maxFontSize.value
                var best = low
                repeat(7) {
                    val mid = (low + high) / 2f
                    val measuredHeight = measureTextHeight(
                        measurer = measurer,
                        text = text,
                        fontSizeSp = mid,
                        fontWeight = fontWeight,
                        constraints = constraints,
                        layoutDirection = layoutDirection
                    )
                    if (measuredHeight <= availableHeight) {
                        best = mid
                        low = mid
                    } else {
                        high = mid
                    }
                }
                TextUnit(
                    best.coerceIn(minFontSize.value, maxFontSize.value),
                    TextUnitType.Sp
                )
            }
        }

        Text(
            text = text,
            fontSize = fontSize,
            lineHeight = fontSize * 1.3f,
            fontWeight = fontWeight,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun measureTextHeight(
    measurer: TextMeasurer,
    text: String,
    fontSizeSp: Float,
    fontWeight: FontWeight,
    constraints: Constraints,
    layoutDirection: androidx.compose.ui.unit.LayoutDirection
): Int {
    val fontSize = TextUnit(fontSizeSp, TextUnitType.Sp)
    return measurer.measure(
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            lineHeight = fontSize * 1.3f
        ),
        constraints = constraints,
        layoutDirection = layoutDirection
    ).size.height
}

@Composable
private fun EmptyReviewState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Mint,
            modifier = Modifier.size(88.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Nothing to review",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add a few words and come back for a quick review session.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LeitnerResult(
    correct: Int,
    incorrect: Int,
    total: Int,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = if (accuracy >= 70) listOf(Mint, Cyan) else listOf(Coral, WarmOrange)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$accuracy%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when {
                    accuracy >= 80 -> "Perfect!"
                    accuracy >= 60 -> "Well done!"
                    else -> "Keep practicing!"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$correct",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Mint,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Knew it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$incorrect",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Coral,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Missed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry", color = MaterialTheme.colorScheme.onBackground)
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Done")
                }
            }
        }
    }
}

private const val CARD_HEIGHT_FRACTION = 0.68f
private const val HANDOFF_DELAY_MS = 90L
private const val RESULT_DELAY_MS = 260L
