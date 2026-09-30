package com.lexora.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
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
            Text(stringResource(R.string.search), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text(stringResource(R.string.search_all_hint), color = MaterialTheme.colorScheme.outline) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel.search("")
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = PrimaryBlue,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = PrimaryBlue
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("${stringResource(R.string.words)} (${uiState.words.size})", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("${stringResource(R.string.grammar)} (${uiState.grammar.size})", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("${stringResource(R.string.notes)} (${uiState.notes.size})", modifier = Modifier.padding(12.dp))
            }
        }

        when (selectedTab) {
            0 -> {
                if (uiState.words.isEmpty() && searchQuery.isNotBlank()) {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = stringResource(R.string.no_words_found),
                        message = stringResource(R.string.try_different_search),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.words, key = { it.id }) { word ->
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
                if (uiState.grammar.isEmpty() && searchQuery.isNotBlank()) {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = stringResource(R.string.no_grammar_found),
                        message = stringResource(R.string.try_different_search),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.grammar, key = { it.id }) { grammar ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = grammar.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        if (grammar.category.isNotBlank()) {
                                            CategoryChip(text = grammar.category, color = PurpleAccent)
                                        }
                                    }
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
                if (uiState.notes.isEmpty() && searchQuery.isNotBlank()) {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = stringResource(R.string.no_notes_found),
                        message = stringResource(R.string.try_different_search),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PushPin, contentDescription = null, tint = Cyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        if (note.category.isNotBlank()) {
                                            CategoryChip(text = note.category, color = Cyan)
                                        }
                                    }
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
