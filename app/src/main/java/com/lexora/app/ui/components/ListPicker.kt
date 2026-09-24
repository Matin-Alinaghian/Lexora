package com.lexora.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lexora.app.ui.theme.*

@Composable
fun ListPicker(
    label: String,
    selectedList: String,
    onListSelected: (String) -> Unit,
    existingLists: List<String>,
    onRenameList: (oldName: String, newName: String) -> Unit = { _, _ -> },
    accentColor: Color = PrimaryBlue
) {
    var showDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Surface(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedList.ifBlank { "Uncategorized (Tap to select)" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedList.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select List") },
            text = {
                Column {
                    Button(
                        onClick = { 
                            showDialog = false
                            showCreateDialog = true 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New List")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (existingLists.isEmpty()) {
                        Text(
                            "No lists created yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            item {
                                ListOptionItem(
                                    name = "Uncategorized",
                                    isSelected = selectedList.isBlank(),
                                    onClick = {
                                        onListSelected("")
                                        showDialog = false
                                    },
                                    accentColor = accentColor
                                )
                            }
                            items(existingLists) { listName ->
                                ListOptionItem(
                                    name = listName,
                                    isSelected = selectedList == listName,
                                    onClick = {
                                        onListSelected(listName)
                                        showDialog = false
                                    },
                                    onEditClick = {
                                        showRenameDialog = listName
                                    },
                                    accentColor = accentColor
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showCreateDialog) {
        var newListName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New List") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("List Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            onListSelected(newListName.trim())
                            showCreateDialog = false
                        }
                    },
                    enabled = newListName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    showRenameDialog?.let { oldName ->
        var newName by remember { mutableStateOf(oldName) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename List") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newName != oldName) {
                            onRenameList(oldName, newName.trim())
                            if (selectedList == oldName) {
                                onListSelected(newName.trim())
                            }
                            showRenameDialog = null
                        }
                    },
                    enabled = newName.isNotBlank() && newName != oldName
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ListOptionItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = accentColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (onEditClick != null) {
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Rename",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
