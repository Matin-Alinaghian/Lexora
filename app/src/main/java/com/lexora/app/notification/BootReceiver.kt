package com.lexora.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reviewScheduleManager: ReviewScheduleManager

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "android.intent.action.MY_PACKAGE_REPLACED" -> {
                val scheduler = ReminderScheduler(context)
                ReminderScheduler.rescheduleFromPrefs(context)
                scheduler.rescheduleLeitnerFromPrefs()
                reviewScheduleManager.refresh()
            }
        }
    }
}
