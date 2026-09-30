package com.lexora.app.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.R
import com.lexora.app.ui.components.ListPicker
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGrammarScreen(
    navController: NavController,
    viewModel: AddEditGrammarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val soundManager = LocalSoundManager.current

    var isDetailsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            soundManager.playSound(SoundManager.SoundType.SUCCESS)
            navController.popBackStack()
        }
    }

    val isEditing = uiState.isEditing
    val showDetails = isDetailsExpanded || isEditing || uiState.explanation.isNotBlank()

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
                    if (uiState.isEditing) stringResource(R.string.edit_grammar_title) else stringResource(R.string.add_grammar),
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { viewModel.saveGrammar() },
                    enabled = uiState.title.isNotBlank()
                ) {
                    Text(stringResource(R.string.form_save), color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            }

                                    Text(
                text = stringResource(R.string.form_basics),
                style = MaterialTheme.typography.titleMedium,
                color = PurpleAccent,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FormField(
                label = stringResource(R.string.form_title_req),
                value = uiState.title,
                onValueChange = { viewModel.updateField("title", it) },
                placeholder = "",
                minLines = 1
            )

            ListPicker(
                label = stringResource(R.string.form_list_category),
                selectedList = uiState.category,
                onListSelected = { viewModel.updateField("category", it) },
                existingLists = uiState.existingLists,
                onRenameList = { old, new -> viewModel.renameList(old, new) },
                accentColor = PurpleAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

                        if (showDetails) {
                Text(
                    text = stringResource(R.string.form_details),
                    style = MaterialTheme.typography.titleMedium,
                    color = Cyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FormField(
                    label = stringResource(R.string.explanation),
                    value = uiState.explanation,
                    onValueChange = { viewModel.updateField("explanation", it) },
                    placeholder = "",
                    minLines = 3
                )

                                FormField(
                    label = stringResource(R.string.form_positive),
                    value = uiState.positiveForm,
                    onValueChange = { viewModel.updateField("positiveForm", it) },
                    placeholder = ""
                )

                                FormField(
                    label = stringResource(R.string.form_negative),
                    value = uiState.negativeForm,
                    onValueChange = { viewModel.updateField("negativeForm", it) },
                    placeholder = ""
                )

                                FormField(
                    label = stringResource(R.string.form_question),
                    value = uiState.questionForm,
                    onValueChange = { viewModel.updateField("questionForm", it) },
                    placeholder = ""
                )

                                FormField(
                    label = stringResource(R.string.form_examples_sep),
                    value = uiState.examples,
                    onValueChange = { viewModel.updateField("examples", it) },
                    placeholder = "",
                    minLines = 3
                )

                                FormField(
                    label = stringResource(R.string.form_tips),
                    value = uiState.tips,
                    onValueChange = { viewModel.updateField("tips", it) },
                    placeholder = stringResource(R.string.form_tips_hint),
                    minLines = 2
                )
            } else {
                TextButton(
                    onClick = { isDetailsExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.form_show_more))
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
                focusedBorderColor = PurpleAccent,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = PurpleAccent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium,
            minLines = minLines
        )
    }
}
