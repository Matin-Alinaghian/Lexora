package com.lexora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.lexora.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.components.LevelBadge
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.BlueGradientEnd
import com.lexora.app.ui.theme.BlueGradientStart
import com.lexora.app.ui.theme.CoffeeBronze
import com.lexora.app.ui.theme.CoffeeGold
import com.lexora.app.ui.theme.CoffeeLatte
import com.lexora.app.ui.theme.CoffeeLinen
import com.lexora.app.ui.theme.CoffeeMocha
import com.lexora.app.ui.theme.CoffeeSand
import com.lexora.app.ui.theme.Coral
import com.lexora.app.ui.theme.Gold
import com.lexora.app.ui.theme.LightCyan
import com.lexora.app.ui.theme.Mint
import com.lexora.app.ui.theme.PrimaryBlue
import com.lexora.app.ui.theme.SunsetGradientEnd
import com.lexora.app.ui.theme.SunsetGradientStart
import com.lexora.app.ui.theme.ThemeManager
import com.lexora.app.ui.theme.ThemeType
import com.lexora.app.ui.theme.WarmOrange
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundManager = LocalSoundManager.current
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val isLight = ThemeManager.getTheme() == ThemeType.LIGHT

    LaunchedEffect(Unit) {
        viewModel.recordAppEntry()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lexora",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = subTextColor,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LevelBadge(xp = uiState.totalXp)

        Spacer(modifier = Modifier.height(20.dp))

        GlassCard(
            gradient = if (isLight) {
                Brush.linearGradient(colors = listOf(CoffeeLinen, CoffeeSand))
            } else {
                Brush.linearGradient(colors = listOf(BlueGradientStart, BlueGradientEnd))
            }
        ) {
            Text(
                text = stringResource(R.string.home_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                StatItem(
                    icon = Icons.Filled.Timer,
                    label = stringResource(R.string.home_minutes),
                    value = uiState.studyTimeMinutes.toString(),
                    color = if (isLight) CoffeeMocha else LightCyan,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = stringResource(R.string.home_words),
                    value = uiState.wordsStudied.toString(),
                    color = if (isLight) CoffeeLatte else Mint,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Filled.Quiz,
                    label = stringResource(R.string.home_reviewed),
                    value = uiState.testsTaken.toString(),
                    color = if (isLight) CoffeeBronze else WarmOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(
            gradient = if (isLight) {
                Brush.linearGradient(colors = listOf(Color.White, CoffeeLinen))
            } else {
                Brush.linearGradient(colors = listOf(SunsetGradientStart, SunsetGradientEnd))
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isLight) CoffeeGold.copy(alpha = 0.15f) else Gold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (isLight) CoffeeGold else Gold,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_streak_title, uiState.currentStreak),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                    Text(
                        text = stringResource(R.string.home_streak_active, uiState.streakPercent, uiState.totalActiveDays),
                        style = MaterialTheme.typography.bodySmall,
                        color = subTextColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Filled.Collections,
                title = stringResource(R.string.home_quick_review),
                subtitle = stringResource(R.string.home_quick_review_subtitle),
                color = if (isLight) CoffeeMocha else PrimaryBlue,
                modifier = Modifier.weight(1f),
            ) {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.LeitnerReview.route + "?mode=all")
            }
            QuickActionCard(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = stringResource(R.string.home_dictionary),
                subtitle = stringResource(R.string.home_dictionary_subtitle),
                color = if (isLight) CoffeeBronze else Gold,
                modifier = Modifier.weight(1f),
            ) {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Dictionary.route)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Filled.Quiz,
                title = stringResource(R.string.home_quiz),
                subtitle = stringResource(R.string.home_quiz_subtitle),
                color = if (isLight) CoffeeLatte else WarmOrange,
                modifier = Modifier.weight(1f),
            ) {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Quiz.route)
            }
            QuickActionCard(
                icon = Icons.Filled.ErrorOutline,
                title = stringResource(R.string.home_mistakes),
                subtitle = stringResource(R.string.home_mistakes_subtitle),
                color = if (isLight) CoffeeBronze else Coral,
                modifier = Modifier.weight(1f),
            ) {
                soundManager.playSound(SoundManager.SoundType.POPUP)
                navController.navigate(Screen.Mistakes.route)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    GlassCard(
        modifier = modifier
            .heightIn(min = 116.dp)
            .clickable {
                soundManager.playSound(SoundManager.SoundType.CLICK)
                onClick()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
