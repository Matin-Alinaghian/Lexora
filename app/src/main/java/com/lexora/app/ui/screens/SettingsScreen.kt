package com.lexora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.lexora.app.ui.components.GlassCard

import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.NotificationPermissionHelper
import com.lexora.app.utils.SoundManager

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val soundManager = LocalSoundManager.current
    val context = LocalContext.current

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshNotificationPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var themeType by remember { mutableStateOf(ThemeManager.getTheme()) }
    val isLight = themeType == ThemeType.LIGHT

    LaunchedEffect(themeType) {
        ThemeManager.setTheme(themeType)
        viewModel.setTheme(themeType)
    }

    val bgColor = if (isLight) LightBackground else DarkBackground
    val textColor = if (isLight) Color.Black else TextPrimary
    val subTextColor = if (isLight) Color(0xFF333333) else TextSecondary
    val sectionTitleColor = if (isLight) MaterialTheme.colorScheme.primary else PrimaryBlue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(20.dp))

        SettingsSection(title = "Theme", titleColor = sectionTitleColor) {
            ThemeSelector(
                currentTheme = themeType,
                onThemeSelected = { themeType = it },
                textColor = textColor,
                subTextColor = subTextColor
            )
            SettingsToggleItem(
                icon = Icons.Filled.Animation,
                title = "Animations",
                subtitle = "Enable smooth animations",
                checked = uiState.animationEnabled,
                onCheckedChange = { viewModel.updateAnimationEnabled(it) },
                textColor = textColor,
                subTextColor = subTextColor,
                isLight = isLight
            )
            SettingsToggleItem(
                icon = Icons.Filled.VolumeUp,
                title = "Sound Effects",
                subtitle = "Play sounds on interactions",
                checked = uiState.soundEnabled,
                onCheckedChange = { viewModel.updateSoundEnabled(it) },
                textColor = textColor,
                subTextColor = subTextColor,
                isLight = isLight
            )
            SettingsToggleItem(
                icon = Icons.Filled.MusicNote,
                title = "Background Music",
                subtitle = "Play relaxing ambient music",
                checked = uiState.musicEnabled,
                onCheckedChange = { viewModel.updateMusicEnabled(it) },
                textColor = textColor,
                subTextColor = subTextColor,
                isLight = isLight
            )
            if (uiState.musicEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.VolumeDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Music Volume", style = MaterialTheme.typography.bodyLarge, color = textColor)
                        Slider(
                            value = uiState.musicVolume,
                            onValueChange = { viewModel.updateMusicVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        if (!uiState.notificationPermissionGranted) {
            val warnContainer = if (isLight) Color(0xFFFFF7E0) else Color(0xFF3A2E12)
            val warnBorder = if (isLight) Color(0xFFF2B705) else Gold.copy(alpha = 0.6f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(warnContainer, warnContainer.copy(alpha = 0.85f)))
                    )
                    .border(1.dp, warnBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFFB8860B) else Gold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Notifications are blocked",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        "Study reminders can't reach you. Tap to allow notifications in system settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = subTextColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = {
                        soundManager.playSound(SoundManager.SoundType.CLICK)
                        NotificationPermissionHelper.openNotificationSettings(context)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Allow")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        SettingsSection(title = "Notifications", titleColor = sectionTitleColor) {
            SettingsToggleItem(
                icon = Icons.Filled.Notifications,
                title = "Daily Reminders",
                subtitle = "Get a daily reminder to practice",
                checked = uiState.notificationsEnabled,
                onCheckedChange = { viewModel.updateNotificationsEnabled(it) },
                textColor = textColor,
                subTextColor = subTextColor,
                isLight = isLight
            )
        }

        SettingsSection(title = "Smart Review Reminder", titleColor = sectionTitleColor) {
            SettingsToggleItem(
                icon = Icons.Filled.FlashOn,
                title = "Review Reminder",
                subtitle = "Reminder when your words are ready for review",
                checked = uiState.leitnerReminderEnabled,
                onCheckedChange = { viewModel.updateLeitnerReminderEnabled(it) },
                textColor = textColor,
                subTextColor = subTextColor,
                isLight = isLight
            )
        }

        var showAboutDialog by remember { mutableStateOf(false) }

        SettingsSection(title = "About", titleColor = sectionTitleColor) {
            SettingsClickItem(
                icon = Icons.Filled.Info,
                title = "About Lexora",
                subtitle = "Version 1.0.0",
                onClick = { showAboutDialog = true },
                textColor = textColor,
                subTextColor = subTextColor
            )
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                containerColor = if (isLight) LightCard else DarkCard,
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "About Lexora",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lexora",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Learn. Practice. Become Fluent.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "A professional English learning app with an offline bilingual dictionary, quick review, quizzes, and a beautiful modern design.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val features = listOf(
                            "\uD83D\uDCE6 Offline Dictionary \u2014 300,000+ bilingual entries with phonetics",
                            "\uD83E\uDDE0 Quick Review \u2014 Practice random words or a single list, any time",
                            "\uD83D\uDD01 Review Reminder \u2014 We notify you when words are ready to revisit",
                            "\uD83D\uDCDA Vocabulary, Grammar & Notes \u2014 Your full learning library",
                            "\u2753 Quiz \u2014 Test yourself with 4-choice questions",
                            "\uD83D\uDD01 Mistakes \u2014 Re-practice the words you got wrong",
                            "\u2B50 Favorites \u2014 Star and revisit your important words",
                            "\uD83D\uDCCA Statistics \u2014 Streaks, accuracy breakdown and progress tracking",
                            "\uD83D\uDD14 Smart Reminders \u2014 Daily study plus timely review notifications",
                            "\uD83D\uDD0A Text-to-Speech & Sound Effects \u2014 Pronunciation and interactive sounds",
                            "\uD83C\uDFA8 Light & Dark Themes \u2014 A clean, modern look"
                        )

                        features.forEach { feature ->
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodySmall,
                                color = subTextColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Created by Matin Alinaghian",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isLight) CoffeeBronze else Gold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(
                            onClick = { showAboutDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Close", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lexora",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Learn. Practice. Become Fluent.",
                style = MaterialTheme.typography.bodySmall,
                color = subTextColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLight) {
            Text(
                text = "Created by Matin Alinaghian",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryBlue.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "Created by Matin Alinaghian",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    shadow = Shadow(
                        color = PrimaryBlue.copy(alpha = 0.6f),
                        blurRadius = 8f
                    )
                ),
                color = LightCyan,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    titleColor: Color = PrimaryBlue,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = titleColor,
        modifier = Modifier.padding(bottom = 10.dp, top = 4.dp)
    )
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        content()
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textColor: Color = TextPrimary,
    subTextColor: Color = TextSecondary,
    isLight: Boolean = false,
    enabled: Boolean = true
) {
    val offTrackColor = if (isLight) Color(0xFFD1D5DB) else DarkCardElevated

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = subTextColor)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = offTrackColor
            )
        )
    }
}

@Composable
private fun ThemeSelector(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    textColor: Color = TextPrimary,
    subTextColor: Color = TextSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Theme Mode", style = MaterialTheme.typography.bodyLarge, color = textColor)
            Text("Select light or dark theme", style = MaterialTheme.typography.bodySmall, color = subTextColor)
        }
        Row {
            FilterChip(
                selected = currentTheme == ThemeType.LIGHT,
                onClick = { onThemeSelected(ThemeType.LIGHT) },
                label = { Text("Light", color = textColor) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = currentTheme == ThemeType.DARK,
                onClick = { onThemeSelected(ThemeType.DARK) },
                label = { Text("Dark", color = textColor) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavyBlue,
                    selectedLabelColor = LightBlue
                )
            )
        }
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = TextPrimary,
    subTextColor: Color = TextSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = subTextColor)
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = subTextColor
        )
    }
}
