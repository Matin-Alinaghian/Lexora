package com.lexora.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.data.repository.SeedStatus
import com.lexora.app.data.remote.dictionary.DefinitionItem
import com.lexora.app.data.remote.dictionary.DictionaryResult
import com.lexora.app.R
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    navController: NavController,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val seedStatus by viewModel.seedStatus.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
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
            Text(stringResource(R.string.dictionary_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        }

                Column(
            modifier = Modifier.fillMaxSize()
        ) {
                        OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(R.string.dict_search_hint),
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

                        DictionaryStatusBanner(status = seedStatus)

                        if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.dict_searching),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

                        uiState.error?.let { error ->
                if (!uiState.isLoading) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

                        if (uiState.results.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = uiState.results,
                        key = { _, result -> result.word + result.partOfSpeech }
                    ) { index, result ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(result.word) { visible = true }

                        val entryAlpha by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 250,
                                delayMillis = index * 60
                            ),
                            label = "entryAlpha"
                        )
                        val entryOffsetY by animateFloatAsState(
                            targetValue = if (visible) 0f else 40f,
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "entryOffsetY"
                        )

                        DictionaryResultCard(
                            result = result,
                            isExpanded = uiState.selectedResult == result,
                            onClick = {
                                soundManager.playSound(SoundManager.SoundType.CLICK)
                                viewModel.selectResult(
                                    if (uiState.selectedResult == result) null else result
                                )
                            },
                            onSpeak = { viewModel.speak(result.word, result.audioUrl) },
                            modifier = Modifier.graphicsLayer {
                                alpha = entryAlpha
                                translationY = entryOffsetY
                            }
                        )
                    }
                }
            } else if (!uiState.isLoading && uiState.searchQuery.length >= 2 && uiState.error == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.dict_min_chars),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DictionaryResultCard(
    result: DictionaryResult,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
        val cardScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.01f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clickable { onClick() }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessMedium
                )
            )
    ) {
        Column {
                        Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                        text = result.word,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                                        if (result.phonetic.isNotBlank()) {
                        Text(
                            text = result.phonetic,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                                IconButton(onClick = onSpeak) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                                val expandRotation by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
                    label = "expandRotation"
                )
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { rotationZ = expandRotation }
                )
            }

                        if (result.persianTranslation.isNotBlank()) {
                var persianVisible by remember { mutableStateOf(false) }
                LaunchedEffect(result.word) { persianVisible = true }
                val persianAlpha by animateFloatAsState(
                    targetValue = if (persianVisible) 1f else 0f,
                    animationSpec = tween(300, delayMillis = 100),
                    label = "persianAlpha"
                )
                val persianOffsetX by animateFloatAsState(
                    targetValue = if (persianVisible) 0f else -20f,
                    animationSpec = spring(dampingRatio = 0.8f),
                    label = "persianOffsetX"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.graphicsLayer {
                        alpha = persianAlpha
                        translationX = persianOffsetX
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇮🇷",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.persianTranslation,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

                        Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (result.partOfSpeech.isNotBlank()) {
                    PartOfSpeechChip(text = result.partOfSpeech)
                }
                if (result.level.isNotBlank()) {
                    LevelChip(level = result.level)
                }
            }

                        AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                                        if (result.definitions.isNotEmpty()) {
                        SectionHeader(icon = Icons.Filled.MenuBook, title = stringResource(R.string.dict_definitions))
                        result.definitions.forEachIndexed { index, def ->
                            DefinitionItem(
                                index = index + 1,
                                definition = def.definition,
                                example = def.example
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                                        if (result.persianMeanings.size > 1) {
                        SectionHeader(icon = Icons.Filled.Translate, title = stringResource(R.string.dict_persian_meanings))
                        WordChipGroup(words = result.persianMeanings, color = Cyan)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                                        if (result.examples.isNotEmpty()) {
                        SectionHeader(icon = Icons.Filled.FormatQuote, title = stringResource(R.string.dict_examples))
                        result.examples.forEach { example ->
                            ExampleItem(text = example)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                                        if (result.synonyms.isNotEmpty()) {
                        SectionHeader(icon = Icons.Filled.ThumbUp, title = stringResource(R.string.synonyms))
                        WordChipGroup(words = result.synonyms, color = Mint)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                                        if (result.antonyms.isNotEmpty()) {
                        SectionHeader(icon = Icons.Filled.ThumbDown, title = stringResource(R.string.antonyms))
                        WordChipGroup(words = result.antonyms, color = Coral)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                                        val groupedMeanings = result.definitions
                        .groupBy { it.partOfSpeech }
                        .filter { it.key != result.partOfSpeech }

                    if (groupedMeanings.isNotEmpty()) {
                        groupedMeanings.forEach { (pos, definitions) ->
                            SectionHeader(
                                icon = Icons.Filled.Category,
                                title = stringResource(R.string.dict_other_meanings, pos)
                            )
                            definitions.take(2).forEach { def ->
                                DefinitionItem(
                                    index = 1,
                                    definition = def.definition,
                                    example = def.example
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DictionaryStatusBanner(status: SeedStatus) {
    when {
        status.isSeeding -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (status.expected > 0) {
                            "${stringResource(R.string.dict_loading)} ${status.inserted} / ${status.expected}"
                        } else {
                            status.message.ifBlank { stringResource(R.string.dict_loading) }
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (status.expected > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { status.progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        status.totalWords > 0 -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.OfflinePin,
                    contentDescription = null,
                    tint = Mint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.dict_offline_count, status.totalWords),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        status.failed -> {
            Text(
                text = status.message,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DefinitionItem(index: Int, definition: String, example: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (example.isNotBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "\"",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = example,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun ExampleItem(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "\"$text\"",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun PartOfSpeechChip(text: String) {
    Surface(
        color = PrimaryBlue.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LevelChip(level: String) {
    val color = when (level) {
        "A1" -> Mint
        "A2" -> Cyan
        "B1" -> PrimaryBlue
        "B2" -> PurpleAccent
        "C1" -> WarmOrange
        "C2" -> Coral
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                Icons.Filled.SignalCellularAlt,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = level,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun WordChipGroup(words: List<String>, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        words.chunked(3).forEach { rowWords ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowWords.forEach { word ->
                    Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = word,
                            style = MaterialTheme.typography.bodySmall,
                            color = color,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
