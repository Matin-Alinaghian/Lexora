package com.lexora.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
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
            Text(stringResource(R.string.favorites), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Gold
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text(stringResource(R.string.words), modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text(stringResource(R.string.grammar), modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text(stringResource(R.string.notes), modifier = Modifier.padding(12.dp))
            }
        }

        when (selectedTab) {
            0 -> {
                if (uiState.favoriteWords.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Star,
                        title = stringResource(R.string.no_fav_words),
                        message = stringResource(R.string.star_words_msg),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favoriteWords, key = { it.id }) { word ->
                            WordCard(
                                word = word,
                                onClick = { navController.navigate(Screen.WordDetail.createRoute(word.id)) },
                                onFavoriteClick = { viewModel.toggleWordFavorite(word) },
                                onDeleteClick = {}
                            )
                        }
                    }
                }
            }
            1 -> {
                if (uiState.favoriteGrammar.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Star,
                        title = stringResource(R.string.no_fav_grammar),
                        message = stringResource(R.string.star_grammar_msg),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favoriteGrammar, key = { it.id }) { grammar ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = grammar.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                if (grammar.category.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CategoryChip(text = grammar.category, color = PurpleAccent)
                                }
                                TextButton(onClick = {
                                    navController.navigate(Screen.GrammarDetail.createRoute(grammar.id))
                                }) {
                                    Text(stringResource(R.string.view), color = PurpleAccent)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                if (uiState.favoriteNotes.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Star,
                        title = stringResource(R.string.no_fav_notes),
                        message = stringResource(R.string.star_notes_msg),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favoriteNotes, key = { it.id }) { note ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PushPin, contentDescription = null, tint = Cyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                TextButton(onClick = {
                                    navController.navigate(Screen.NoteDetail.createRoute(note.id))
                                }) {
                                    Text(stringResource(R.string.view), color = Cyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
