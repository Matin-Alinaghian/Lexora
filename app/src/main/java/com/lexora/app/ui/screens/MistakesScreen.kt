package com.lexora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MistakesScreen(
    navController: NavController,
    viewModel: MistakesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            Text(stringResource(R.string.mistakes), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        }

        if (uiState.mistakeWords.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.CheckCircle,
                title = stringResource(R.string.no_mistakes_title),
                message = stringResource(R.string.no_mistakes_msg),
                modifier = Modifier.weight(1f)
            )
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { navController.navigate(Screen.LeitnerReview.route + "?mode=mistakes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.FlashOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.review_mistakes))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.mistakeWords, key = { it.id }) { word ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = word.englishWord,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = word.persianMeaning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                                                IconButton(
                                    onClick = { viewModel.markAsKnown(word.id) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Mint.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "I know this",
                                        tint = Mint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
