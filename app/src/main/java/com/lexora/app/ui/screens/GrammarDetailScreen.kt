package com.lexora.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.R
import com.lexora.app.ui.components.*
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarDetailScreen(
    navController: NavController,
    viewModel: GrammarDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            show = true,
            onConfirm = {
                viewModel.deleteGrammar()
                navController.popBackStack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    uiState.grammar?.let { grammar ->
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
                Text(grammar.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.toggleLeitner() }) {
                    Icon(
                        if (grammar.isInLeitner) Icons.Filled.FlashOn else Icons.Outlined.FlashOn,
                        contentDescription = "Leitner",
                        tint = if (grammar.isInLeitner) SunsetGradientStart else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        if (grammar.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (grammar.isFavorite) Gold else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = {
                    navController.navigate(Screen.EditGrammar.createRoute(grammar.id))
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
                if (grammar.category.isNotBlank()) {
                    CategoryChip(text = grammar.category, color = PurpleAccent)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (grammar.explanation.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.explanation), style = MaterialTheme.typography.titleMedium, color = PurpleAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(grammar.explanation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.speakText(grammar.explanation) }) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                        Text(stringResource(R.string.detail_tap_listen), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (grammar.positiveForm.isNotBlank() || grammar.negativeForm.isNotBlank() || grammar.questionForm.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.formula), style = MaterialTheme.typography.titleMedium, color = Cyan)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (grammar.positiveForm.isNotBlank()) {
                            FormulaBox(label = stringResource(R.string.form_positive), content = grammar.positiveForm, color = Success)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (grammar.negativeForm.isNotBlank()) {
                            FormulaBox(label = stringResource(R.string.form_negative), content = grammar.negativeForm, color = Error)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (grammar.questionForm.isNotBlank()) {
                            FormulaBox(label = stringResource(R.string.form_question), content = grammar.questionForm, color = Warning)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (grammar.isInLeitner) {
                    val boxColors = listOf(
                        Color(0xFFE91E63),                         Color(0xFFFF9800),                         Color(0xFFFFEB3B),                         Color(0xFF4CAF50),                         Color(0xFF2196F3),                     )
                    val boxColor = boxColors.getOrElse(grammar.leitnerBox - 1) { Color.Gray }
                    
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = SunsetGradientStart)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.leitner_review), style = MaterialTheme.typography.titleMedium, color = SunsetGradientStart)
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
                                        "B${grammar.leitnerBox}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = boxColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.detail_leitner_box, grammar.leitnerBox),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (grammar.tips.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Gold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.form_tips), style = MaterialTheme.typography.titleMedium, color = Gold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(grammar.tips, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                                if (grammar.examples.isNotBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.dict_examples), style = MaterialTheme.typography.titleMedium, color = Mint)
                        Spacer(modifier = Modifier.height(8.dp))
                        grammar.examples.split("|").map { it.trim() }.filter { it.isNotBlank() }.forEach { example ->
                            Text(
                                text = "• $example",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    } ?: LoadingState()
}

@Composable
private fun FormulaBox(
    label: String,
    content: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
