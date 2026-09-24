package com.lexora.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.ui.components.ListPicker
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordScreen(
    navController: NavController,
    viewModel: AddEditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val soundManager = LocalSoundManager.current

    var isDetailsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            soundManager.playSound(SoundManager.SoundType.SUCCESS)
            navController.popBackStack()
        }
    }

    val isEditing = uiState.isEditing
    val showDetails = isDetailsExpanded || isEditing || uiState.pronunciation.isNotBlank()

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            Text(
                if (uiState.isEditing) "Edit Word" else "Add Word",
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = { viewModel.saveWord() },
                enabled = uiState.englishWord.isNotBlank() && uiState.persianMeaning.isNotBlank()
            ) {
                Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

                                Text(
                text = "The basics (mandatory)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FormField(
                label = "English Word *",
                value = uiState.englishWord,
                onValueChange = { 
                    viewModel.updateField("englishWord", it)
                    if (it.length >= 3) {
                        viewModel.autoFillFromDictionary()
                    }
                },
                placeholder = ""
            )

            FormField(
                label = "Persian Meaning *",
                value = uiState.persianMeaning,
                onValueChange = { viewModel.updateField("persianMeaning", it) },
                placeholder = ""
            )

            ListPicker(
                label = "List / Category",
                selectedList = uiState.category,
                onListSelected = { viewModel.updateField("category", it) },
                existingLists = uiState.existingLists,
                onRenameList = { old, new -> viewModel.renameList(old, new) },
                accentColor = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

                        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = true,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Spaced review (auto)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "This word will come back for review on a schedule",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

                        Button(
                onClick = { 
                    viewModel.autoFillFromDictionary()
                    isDetailsExpanded = true
                },
                enabled = uiState.englishWord.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search & Auto-fill from Dictionary")
            }

            Spacer(modifier = Modifier.height(16.dp))

                        if (showDetails) {
                Text(
                    text = "Additional details",
                    style = MaterialTheme.typography.titleMedium,
                    color = Cyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                                FormField(
                    label = "Pronunciation",
                    value = uiState.pronunciation,
                    onValueChange = { viewModel.updateField("pronunciation", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Word Type",
                    value = uiState.wordType,
                    onValueChange = { viewModel.updateField("wordType", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Level",
                    value = uiState.level,
                    onValueChange = { viewModel.updateField("level", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Example",
                    value = uiState.example,
                    onValueChange = { viewModel.updateField("example", it) },
                    placeholder = "",
                    minLines = 2
                )

                                FormField(
                    label = "Example Translation",
                    value = uiState.exampleTranslation,
                    onValueChange = { viewModel.updateField("exampleTranslation", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Synonyms (comma separated)",
                    value = uiState.synonyms,
                    onValueChange = { viewModel.updateField("synonyms", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Antonyms (comma separated)",
                    value = uiState.antonyms,
                    onValueChange = { viewModel.updateField("antonyms", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Word Family (comma separated)",
                    value = uiState.wordFamily,
                    onValueChange = { viewModel.updateField("wordFamily", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Tags (comma separated)",
                    value = uiState.tags,
                    onValueChange = { viewModel.updateField("tags", it) },
                    placeholder = ""
                )

                                FormField(
                    label = "Personal Note",
                    value = uiState.personalNote,
                    onValueChange = { viewModel.updateField("personalNote", it) },
                    placeholder = "",
                    minLines = 3
                )
            } else {
                TextButton(
                    onClick = { isDetailsExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show more fields")
                    Icon(Icons.Filled.ExpandMore, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

                        if (uiState.error.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = null, tint = Error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.error, color = Error)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.outline) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium,
            minLines = minLines
        )
    }
}
