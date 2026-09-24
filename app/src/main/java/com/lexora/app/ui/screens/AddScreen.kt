package com.lexora.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lexora.app.ui.components.GlassCard
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@Composable
fun AddScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            imageVector = Icons.Filled.AddCircle,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add new",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Choose what you want to add",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AddOptionCard(
            icon = Icons.Filled.MenuBook,
            title = "Add Word",
            subtitle = "Add a new vocabulary word with meanings, examples, and more",
            gradient = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd)),
            onClick = { navController.navigate(Screen.AddWord.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AddOptionCard(
            icon = Icons.Filled.School,
            title = "Add Grammar",
            subtitle = "Add grammar rules, formulas, and examples",
            gradient = Brush.linearGradient(listOf(PurpleGradientStart, PurpleGradientEnd)),
            onClick = { navController.navigate(Screen.AddGrammar.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AddOptionCard(
            icon = Icons.Filled.StickyNote2,
            title = "Add Note",
            subtitle = "Add teacher notes and class tips",
            gradient = Brush.linearGradient(listOf(CyanGradientStart, CyanGradientEnd)),
            onClick = { navController.navigate(Screen.AddNote.route) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AddOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                soundManager.playSound(SoundManager.SoundType.CLICK)
                onClick() 
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
