package com.lexora.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.R
import com.lexora.app.ui.components.*
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Long?>(null) }

    if (showDeleteDialog && noteToDelete != null) {
        ConfirmDeleteDialog(
            show = true,
            onConfirm = {
                viewModel.deleteNote(noteToDelete!!)
                showDeleteDialog = false
                noteToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                noteToDelete = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                Text(stringResource(R.string.teacher_notes), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchNotes(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.search_notes_hint), color = MaterialTheme.colorScheme.outline) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = Cyan,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text(stringResource(R.string.chip_all)) },
                            leadingIcon = if (uiState.selectedCategory == null) {
                                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) },
                            label = { Text(category) },
                            leadingIcon = if (uiState.selectedCategory == category) {
                                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.count_notes, uiState.notes.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }
                uiState.notes.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Outlined.StickyNote2,
                        title = stringResource(R.string.empty_no_notes_title),
                        message = stringResource(R.string.empty_no_notes_msg),
                        actionText = stringResource(R.string.add_note),
                        onActionClick = { navController.navigate(Screen.AddNote.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.PushPin,
                                                contentDescription = null,
                                                tint = Cyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = note.title,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                        if (note.category.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            CategoryChip(text = note.category, color = Cyan)
                                        }
                                    }
                                    IconButton(onClick = { viewModel.toggleFavorite(note) }) {
                                        Icon(
                                            if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = "Favorite",
                                            tint = if (note.isFavorite) Gold else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = {
                                        navController.navigate(Screen.NoteDetail.createRoute(note.id))
                                    }) {
                                        Text(stringResource(R.string.view), color = Cyan)
                                    }
                                    TextButton(onClick = {
                                        navController.navigate(Screen.EditNote.createRoute(note.id))
                                    }) {
                                        Text(stringResource(R.string.edit), color = PrimaryBlue)
                                    }
                                    TextButton(onClick = {
                                        noteToDelete = note.id
                                        showDeleteDialog = true
                                    }) {
                                        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddNote.route) },
            containerColor = Cyan,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Note")
        }
    }
}
