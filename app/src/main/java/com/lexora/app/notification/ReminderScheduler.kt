package com.lexora.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ReminderScheduler(private val context: Context) {
    companion object {
        private const val ALARM_TYPE = "com.lexora.app.REMINDER"
        private const val REQUEST_CODE = 1001
        private const val LEITNER_REQUEST_CODE = 2001
        private const val PREFS = "lexora_reminder"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"

        
        fun rescheduleFromPrefs(context: Context) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean(KEY_ENABLED, false)
            if (enabled) {
                ReminderScheduler(context).scheduleReminder(true)
            }
        }
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
        action = ALARM_TYPE
    }
    private val pendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        reminderIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    
    fun scheduleReminder(enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply()

        if (!enabled) {
            cancelReminder()
            return
        }

        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now + AlarmManager.INTERVAL_DAY
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    
    fun cancelReminder() {
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    
    fun isReminderScheduled(): Boolean {
        val checkIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            reminderIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return checkIntent != null
    }

    
    private val leitnerPrefs get() = context.getSharedPreferences("lexora_leitner_reminder", Context.MODE_PRIVATE)

    
    fun scheduleLeitnerReminder(enabled: Boolean) {
        leitnerPrefs.edit()
            .putBoolean("enabled", enabled)
            .apply()

        val intent = Intent(context, LeitnerReminderReceiver::class.java).apply {
            action = LeitnerReminderReceiver.ACTION
        }
        val pi = PendingIntent.getBroadcast(
            context,
            LEITNER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            try { alarmManager.cancel(pi) } catch (_: Exception) {}
            return
        }

                val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now + AlarmManager.INTERVAL_DAY
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pi
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pi
            )
        } catch (_: SecurityException) {}
    }

    
    fun rescheduleLeitnerFromPrefs() {
        val p = leitnerPrefs
        scheduleLeitnerReminder(p.getBoolean("enabled", false))
    }

    
    fun sendTestNotification() {
        val tapIntent = Intent(context, com.lexora.app.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            context,
            3001,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val messages = listOf(
            "🔔 It works! Reminders will look like this.",
            "✅ Test successful — Lexora can reach you!",
            "🎉 Notifications are up and running!"
        )

        val notification = androidx.core.app.NotificationCompat.Builder(context, "lexora_study_reminders")
            .setSmallIcon(com.lexora.app.R.drawable.ic_launcher_foreground)
            .setContentTitle("Lexora Test")
            .setContentText(messages.random())
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        try {
            nm.notify(3002, notification)
        } catch (_: SecurityException) {
                                }
    }
}
