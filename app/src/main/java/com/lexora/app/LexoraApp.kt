package com.lexora.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.lexora.app.data.repository.DictionaryDataSeeder
import com.lexora.app.notification.ReviewScheduleManager
import com.lexora.app.utils.TtsManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LexoraApp : Application() {

    @Inject
    lateinit var ttsManager: TtsManager
    
    @Inject
    lateinit var dictionaryDataSeeder: DictionaryDataSeeder

    @Inject
    lateinit var reviewScheduleManager: ReviewScheduleManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        ttsManager.init(this)
        
                dictionaryDataSeeder.seedDictionaryData()
        reviewScheduleManager.refresh()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = getString(R.string.notification_channel_description)
                enableVibration(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "lexora_study_reminders"
    }
}
