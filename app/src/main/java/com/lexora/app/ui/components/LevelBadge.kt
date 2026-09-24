package com.lexora.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lexora.app.ui.theme.*

data class LevelInfo(
    val level: Int,
    val title: String,
    val xpRequired: Int,
    val color: Color,
    val icon: String
)

object LevelSystem {
            private val levels: List<LevelInfo> by lazy {
        val colorPalette = listOf(
            WarmOrange, Cyan, PrimaryBlue, PurpleAccent, Gold,
            Mint, Coral, LightCyan, DeepPurple, Color(0xFFFFD700)
        )
        val names = listOf(
            "Newcomer", "Starter", "Novice", "Beginner", "Learner",
            "Student", "Reader", "Speaker", "Listener", "Writer",
            "Explorer", "Discoverer", "Traveler", "Wanderer", "Seeker",
            "Apprentice", "Learner", "Practitioner", "Trainer", "Apprentice",
            "Scholar", "Student", "Academic", "Researcher", "Intellectual",
            "Thinker", "Analyst", "Evaluator", "Critic", "Philosopher",
            "Expert", "Specialist", "Professional", "Authority", "Master",
            "Veteran", "Seasoned", "Experienced", "Skilled", "Talented",
            "Gifted", "Brilliant", "Exceptional", "Outstanding", "Superb",
            "Excellent", "Superior", "Premium", "Elite", "Champion",
            "Hero", "Legend", "Mythic", "Divine", "Celestial",
            "Cosmic", "Eternal", "Immortal", "Transcendent", "Ascended",
            "Enlightened", "Sage", "Oracle", "Prophet", "Visionary",
            "Genius", "Prodigy", "Virtuoso", "Maestro", "Titan",
            "Colossus", "Behemoth", "Leviathan", "Dragon", "Phoenix",
            "Unicorn", "Griffin", "Pegasus", "Chimera", "Kraken",
            "Titan", "Olympian", "Demigod", "Godlike", "Supreme",
            "Absolute", "Ultimate", "Infinite", "Beyond", "Omega",
            "Alpha", "Prime", "Nexus", "Apex", "Zenith",
            "Pinnacle", "Summit", "Crown", "Throne", "Infinity"
        )
        val icons = listOf(
            "🌱", "🌿", "🍀", "☘️", "🌾",
            "📚", "📖", "🗣️", "👂", "✍️",
            "🗺️", "🧭", "✈️", "🚶", "🔍",
            "⚒️", "🔧", "🎯", "💪", "🏋️",
            "🎓", "👨‍🎓", "👩‍🎓", "🔬", "🧪",
            "💡", "🧠", "⚖️", "🎭", "🏛️",
            "⭐", "🌟", "💫", "✨", "🏆",
            "🎖️", "🏅", "🥇", "🥈", "🥉",
            "💎", "🔮", "👑", "🎯", "🎪",
            "🌈", "☀️", "🌙", "⭐", "🏆",
            "🦸", "🦹", "🧙", "🧑‍🚀", "🛸",
            "🌌", "🪐", "☄️", "🌍", "🌎",
            "🌻", "🌺", "🌸", "🌼", "🌷",
            "🧠", "🔮", "📖", "🎭", "🎵",
            "⚔️", "🛡️", "🏹", "🗡️", "🐉",
            "🦅", "🐺", "🦁", "🐯", "🐋",
            "🎆", "🎇", "✨", "🌟", "💫",
            "👑", "🏛️", "🏰", "🗼", "⛩️",
            "🌈", "☀️", "🌙", "⭐", "🔮"
        )

        (1..100).map { level ->
            val xp = calculateXpForLevel(level)
            LevelInfo(
                level = level,
                title = names.getOrElse(level - 1) { "Level $level" },
                xpRequired = xp,
                color = colorPalette[(level - 1) % colorPalette.size],
                icon = icons.getOrElse(level - 1) { "⭐" }
            )
        }
    }

    
    private fun calculateXpForLevel(level: Int): Int {
        if (level <= 1) return 0
        val base = 50
        val multiplier = 1.12
        return (base * (Math.pow(multiplier, (level - 1).toDouble()))).toInt()
    }

    fun getLevelForXp(xp: Int): LevelInfo {
        return levels.lastOrNull { xp >= it.xpRequired } ?: levels.first()
    }

    fun getNextLevel(currentLevel: Int): LevelInfo? {
        return levels.getOrNull(currentLevel)
    }

    fun getXpProgress(xp: Int): Float {
        val current = getLevelForXp(xp)
        val next = getNextLevel(current.level) ?: return 1f
        val currentXp = xp - current.xpRequired
        val requiredXp = next.xpRequired - current.xpRequired
        if (requiredXp <= 0) return 1f
        return (currentXp.toFloat() / requiredXp).coerceIn(0f, 1f)
    }

    fun getXpForNextLevel(xp: Int): Int {
        val current = getLevelForXp(xp)
        val next = getNextLevel(current.level) ?: return 0
        return next.xpRequired - xp
    }
}

@Composable
fun LevelBadge(
    xp: Int,
    modifier: Modifier = Modifier
) {
    val levelInfo = LevelSystem.getLevelForXp(xp)
    val nextLevel = LevelSystem.getNextLevel(levelInfo.level)
    val progress = LevelSystem.getXpProgress(xp)
    val xpToNext = LevelSystem.getXpForNextLevel(xp)

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "levelScale"
    )

    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
                        Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(levelInfo.color, levelInfo.color.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = levelInfo.icon,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Lv.${levelInfo.level}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = levelInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (nextLevel != null) {
                    Text(
                        text = "$xpToNext XP to ${nextLevel.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Max Level! 👑",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = levelInfo.color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$xp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = levelInfo.color
                )
                Text(
                    text = "XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
