package com.lexora.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.R
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.theme.*

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.statistics),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        val isLight = ThemeManager.getTheme() == ThemeType.LIGHT
        GlassCard(
            gradient = if (isLight) {
                Brush.linearGradient(colors = listOf(CoffeeLinen, CoffeeSand))
            } else {
                Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd))
            }
        ) {
            Text(
                text = stringResource(R.string.stats_week),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(Icons.Filled.Timer, stringResource(R.string.minutes_format, uiState.weekStudyTime), stringResource(R.string.study_time), if (isLight) CoffeeMocha else LightCyan, MaterialTheme.colorScheme.onBackground)
                StatItem(Icons.Filled.MenuBook, "${uiState.weekWords}", stringResource(R.string.words), if (isLight) CoffeeLatte else Mint, MaterialTheme.colorScheme.onBackground)
                StatItem(Icons.Filled.Quiz, "${uiState.weekTests}", stringResource(R.string.tests), if (isLight) CoffeeBronze else WarmOrange, MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        GlassCard(
            gradient = if (isLight) {
                Brush.linearGradient(colors = listOf(Color.White, CoffeeLinen))
            } else {
                Brush.linearGradient(listOf(SunsetGradientStart, SunsetGradientEnd))
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (isLight) CoffeeGold else Gold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.stats_day_streak, uiState.currentStreak),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.stats_best_active, uiState.bestStreak, uiState.activeDays),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.stats_overall),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverallStatItem(stringResource(R.string.words), "${uiState.totalWords}", PrimaryBlue)
                OverallStatItem(stringResource(R.string.active_days), "${uiState.activeDays}", Cyan)
                OverallStatItem(stringResource(R.string.mistakes), "${uiState.mistakeCount}", Coral)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    textColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
    }
}

@Composable
private fun AccuracyBar(label: String, percentage: Int, color: Color, weightLabel: String = "") {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (weightLabel.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = weightLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Text("${percentage}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun OverallStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
