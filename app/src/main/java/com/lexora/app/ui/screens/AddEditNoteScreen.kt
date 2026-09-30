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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            soundManager.playSound(SoundManager.SoundType.SUCCESS)
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
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
                if (uiState.isEditing) stringResource(R.string.edit_note_title) else stringResource(R.string.add_note),
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = { viewModel.saveNote() },
                enabled = uiState.title.isNotBlank() && uiState.content.isNotBlank()
            ) {
                Text(stringResource(R.string.form_save), color = Cyan, fontWeight = FontWeight.Bold)
            }
        }

                    FormField(
                label = stringResource(R.string.form_title_req),
                value = uiState.title,
                onValueChange = { viewModel.updateField("title", it) },
                placeholder = ""
            )

            ListPicker(
                label = stringResource(R.string.form_list_category),
                selectedList = uiState.category,
                onListSelected = { viewModel.updateField("category", it) },
                existingLists = uiState.existingLists,
                onRenameList = { old, new -> viewModel.renameList(old, new) },
                accentColor = Cyan
            )

            FormField(
                label = stringResource(R.string.form_content_req),
                value = uiState.content,
                onValueChange = { viewModel.updateField("content", it) },
                placeholder = "",
                minLines = 5
            )

            FormField(
                label = stringResource(R.string.form_bullets),
                value = uiState.bulletPoints,
                onValueChange = { viewModel.updateField("bulletPoints", it) },
                placeholder = "",
                minLines = 3
            )

            if (uiState.error.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
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
                focusedBorderColor = Cyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = Cyan,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium,
            minLines = minLines
        )
    }
}
