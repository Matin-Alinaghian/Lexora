package com.lexora.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReviewDueScheduler {

    private const val TOLERANCE_MS = 2L * 60 * 60 * 1000
    private const val MIN_RESCHEDULE_DELAY_MS = 30L * 60 * 1000

    fun scheduleNextCheck(context: Context) {
        if (!ReviewDueReceiver.isDueReminderEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReviewDueReceiver::class.java).apply {
            action = ReviewDueReceiver.ACTION
        }
        val pi = PendingIntent.getBroadcast(
            context,
            ReviewDueReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val earliest = getEarliestDueTime(context)
        if (earliest == null) {
            try { alarmManager.cancel(pi) } catch (_: Exception) {}
            return
        }

        val triggerAt = (earliest + TOLERANCE_MS)
            .coerceAtLeast(System.currentTimeMillis() + MIN_RESCHEDULE_DELAY_MS)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (_: SecurityException) {
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } catch (_: Exception) {}
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReviewDueReceiver::class.java).apply {
            action = ReviewDueReceiver.ACTION
        }
        val pi = PendingIntent.getBroadcast(
            context,
            ReviewDueReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) {
            try { alarmManager.cancel(pi) } catch (_: Exception) {}
        }
    }

    fun rescheduleFromPrefs(context: Context) {
        if (ReviewDueReceiver.isDueReminderEnabled(context)) {
            scheduleNextCheck(context)
        }
    }

    private fun getEarliestDueTime(context: Context): Long? {
        return try {
            val prefs = context.getSharedPreferences("lexora_review_due", Context.MODE_PRIVATE)
            val saved = prefs.getLong("earliest_due", -1L)
            if (saved > 0) saved else null
        } catch (_: Exception) {
            null
        }
    }

    fun updateEarliestDueTime(context: Context, nextDueTime: Long) {
        val prefs = context.getSharedPreferences("lexora_review_due", Context.MODE_PRIVATE)
        val current = prefs.getLong("earliest_due", -1L)
        val now = System.currentTimeMillis()

        val expired = current in 1..now

        if (nextDueTime > 0 && (current <= 0 || expired || nextDueTime < current)) {
            prefs.edit().putLong("earliest_due", nextDueTime).apply()
            scheduleNextCheck(context)
        }
    }
}
