package com.lexora.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lexora.app.data.local.entity.WordEntity
import com.lexora.app.ui.theme.*
import androidx.compose.material3.MaterialTheme
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@Composable
fun WordCard(
    word: WordEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundManager = LocalSoundManager.current
    val favoriteColor by animateColorAsState(
        targetValue = if (word.isFavorite) Gold else MaterialTheme.colorScheme.outline,
        label = "favoriteColor"
    )

    GlassCard(
        modifier = modifier.clickable { 
            soundManager.playSound(SoundManager.SoundType.CLICK)
            onClick() 
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.englishWord,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (word.pronunciation.isNotBlank()) {
                        Text(
                            text = "  ${word.pronunciation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    if (word.isInLeitner) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.FlashOn,
                            contentDescription = "In Leitner",
                            tint = SunsetGradientStart,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "B${word.leitnerBox}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SunsetGradientStart,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (word.wordType.isNotBlank() || word.category.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        if (word.wordType.isNotBlank()) {
                            CategoryChip(text = word.wordType, color = PrimaryBlue)
                        }
                        if (word.category.isNotBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            CategoryChip(text = word.category, color = Cyan)
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = {
                        soundManager.playSound(SoundManager.SoundType.POPUP)
                        onFavoriteClick()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (word.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = favoriteColor
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
