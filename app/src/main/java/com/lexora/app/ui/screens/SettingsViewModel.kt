package com.lexora.app.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexora.app.data.repository.DictionaryDataSeeder
import com.lexora.app.data.repository.WordRepository
import com.lexora.app.data.repository.SeedStatus
import com.lexora.app.data.DataExporter
import com.lexora.app.data.ImportResult
import com.lexora.app.notification.ReminderScheduler
import com.lexora.app.notification.ReviewDueReceiver
import com.lexora.app.notification.ReviewDueScheduler
import com.lexora.app.notification.ReviewScheduleManager
import com.lexora.app.ui.theme.ThemeType
import com.lexora.app.utils.StreakManager
import com.lexora.app.utils.SoundManager
import com.lexora.app.utils.BackgroundMusicManager
import com.lexora.app.utils.NotificationPermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val leitnerReminderEnabled: Boolean = true,
    val notificationPermissionGranted: Boolean = true,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 0.3f,
    val animationEnabled: Boolean = true,
    val aiProvider: String = "OpenAI",
    val exportStatus: ExportStatus = ExportStatus.Idle,
    val importStatus: ImportStatus = ImportStatus.Idle,
    val seedStatus: SeedStatus = SeedStatus()
)

sealed class ExportStatus {
    object Idle : ExportStatus()
    object Exporting : ExportStatus()
    data class Success(val file: File) : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}

sealed class ImportStatus {
    object Idle : ImportStatus()
    object Importing : ImportStatus()
    data class Success(val result: ImportResult) : ImportStatus()
    data class Error(val message: String) : ImportStatus()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataExporter: DataExporter,
    private val reminderScheduler: ReminderScheduler,
    private val streakManager: StreakManager,
    private val soundManager: SoundManager,
    private val musicManager: BackgroundMusicManager,
    private val dictionaryDataSeeder: DictionaryDataSeeder,
    private val reviewScheduleManager: ReviewScheduleManager,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
                val prefs = context.getSharedPreferences("lexora_reminder", Context.MODE_PRIVATE)
        val leitnerPrefs = context.getSharedPreferences("lexora_leitner_reminder", Context.MODE_PRIVATE)
        val reviewReminderEnabled = leitnerPrefs.getBoolean("enabled", true)
        _uiState.value = _uiState.value.copy(
            notificationsEnabled = prefs.getBoolean("enabled", true),
            leitnerReminderEnabled = reviewReminderEnabled,
            notificationPermissionGranted = NotificationPermissionHelper.isGranted(context)
        )
        ReviewDueReceiver.setDueReminderEnabled(context, reviewReminderEnabled)

        viewModelScope.launch {
            combine(
                musicManager.musicEnabled,
                musicManager.volume,
                dictionaryDataSeeder.status
            ) { musicEnabled, volume, seedStatus ->
                _uiState.value = _uiState.value.copy(
                    musicEnabled = musicEnabled,
                    musicVolume = volume,
                    seedStatus = seedStatus
                )
            }.collect()
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        reminderScheduler.scheduleReminder(enabled)
    }

    fun updateSoundEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
        soundManager.setSoundEnabled(enabled)
    }

    fun updateMusicEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(musicEnabled = enabled)
        musicManager.setMusicEnabled(enabled)
    }

    fun updateMusicVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(musicVolume = volume)
        musicManager.setMusicVolume(volume)
    }

    fun updateAnimationEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(animationEnabled = enabled)
    }

    fun updateAiProvider(provider: String) {
        _uiState.value = _uiState.value.copy(aiProvider = provider)
    }

    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Exporting)
            try {
                val file = dataExporter.exportData("1.0.0")
                if (file != null) {
                    _uiState.value = _uiState.value.copy(
                        exportStatus = ExportStatus.Success(file)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        exportStatus = ExportStatus.Error("Failed to export data")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exportStatus = ExportStatus.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

    fun setTheme(themeType: ThemeType) {
                    }

    fun importData(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(importStatus = ImportStatus.Importing)
            try {
                val result = dataExporter.importData(file)
                _uiState.value = _uiState.value.copy(
                    importStatus = ImportStatus.Success(result)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    importStatus = ImportStatus.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

    fun resetExportStatus() {
        _uiState.value = _uiState.value.copy(exportStatus = ExportStatus.Idle)
    }

    fun resetImportStatus() {
        _uiState.value = _uiState.value.copy(importStatus = ImportStatus.Idle)
    }

    fun triggerImport() {
                viewModelScope.launch {
            _uiState.value = _uiState.value.copy(importStatus = ImportStatus.Importing)
            try {
                val exportDir = context.getExternalFilesDir(null)
                val latestFile = exportDir?.listFiles()
                    ?.filter { it.name.startsWith("lexora_export") && it.name.endsWith(".json") }
                    ?.maxByOrNull { it.lastModified() }

                if (latestFile != null) {
                    val result = dataExporter.importData(latestFile)
                    _uiState.value = _uiState.value.copy(
                        importStatus = ImportStatus.Success(result)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        importStatus = ImportStatus.Error("No backup file found")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    importStatus = ImportStatus.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

    fun reseedDictionary() {
        viewModelScope.launch {
            dictionaryDataSeeder.reseed()
        }
    }

    
    fun updateLeitnerReminderEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(leitnerReminderEnabled = enabled)
        reminderScheduler.scheduleLeitnerReminder(enabled)
        ReviewDueReceiver.setDueReminderEnabled(context, enabled)
        if (enabled) {
            reviewScheduleManager.refresh()
        } else {
            ReviewDueScheduler.cancel(context)
        }
    }

    
    fun sendTestNotification() {
        reminderScheduler.sendTestNotification()
    }

    
    fun refreshNotificationPermission() {
        _uiState.value = _uiState.value.copy(
            notificationPermissionGranted = NotificationPermissionHelper.isGranted(context)
        )
    }

    
    fun addAllToLeitner() {
        viewModelScope.launch {
            val added = wordRepository.addAllToLeitner()
                    }
    }
}
