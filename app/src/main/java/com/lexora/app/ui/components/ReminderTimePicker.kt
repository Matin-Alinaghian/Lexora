package com.lexora.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.lexora.app.R
import com.lexora.app.ui.theme.DarkCard
import com.lexora.app.ui.theme.DarkBackground
import com.lexora.app.ui.theme.LightCard
import com.lexora.app.ui.theme.LightBackground
import com.lexora.app.ui.theme.PrimaryBlue
import com.lexora.app.ui.theme.TextPrimary
import com.lexora.app.ui.theme.TextSecondary
import com.lexora.app.ui.theme.ThemeManager
import com.lexora.app.ui.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReminderTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeSet: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }
    val isLight = ThemeManager.getTheme() == ThemeType.LIGHT
    val dialogBg = if (isLight) LightCard else DarkCard
    val textColor = if (isLight) Color(0xFF1A1C1E) else TextPrimary
    val subTextColor = if (isLight) Color(0xFF6B7280) else TextSecondary
    val chipSelectedBg = MaterialTheme.colorScheme.primary
    val chipSelectedText = Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.reminder_picker_title),
                color = textColor,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimePicker(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it },
                    textColor = textColor,
                    subTextColor = subTextColor,
                    chipSelectedBg = chipSelectedBg,
                    chipSelectedText = chipSelectedText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSet(hour, minute)
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = subTextColor)
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    textColor: Color,
    subTextColor: Color,
    chipSelectedBg: Color,
    chipSelectedText: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.reminder_picker_msg),
            color = subTextColor,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                        Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.reminder_picker_hour),
                    color = subTextColor,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (0..23).forEach { h ->
                        FilterChip(
                            selected = hour == h,
                            onClick = { onHourChange(h) },
                            label = { Text(String.format(Locale.US, "%02d", h), color = if (hour == h) chipSelectedText else textColor) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipSelectedBg,
                                selectedLabelColor = chipSelectedText
                            )
                        )
                    }
                }
            }

                        Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.reminder_picker_minute),
                    color = subTextColor,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55).forEach { m ->
                        FilterChip(
                            selected = minute == m,
                            onClick = { onMinuteChange(m) },
                            label = { Text(String.format(Locale.US, "%02d", m), color = if (minute == m) chipSelectedText else textColor) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipSelectedBg,
                                selectedLabelColor = chipSelectedText
                            )
                        )
                    }
                }
            }
        }
    }
}
