package com.lexora.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lexora.app.LexoraApp
import com.lexora.app.MainActivity
import com.lexora.app.R
import com.lexora.app.data.local.dao.WordDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LeitnerReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var wordDao: WordDao

    companion object {
        const val ACTION = "com.lexora.app.LEITNER_WEEKLY_REMINDER"
        private const val PREFS = "lexora_leitner_reminder"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("enabled", false)
        if (!enabled) return

                val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val due = wordDao.getLeitnerDueCount(System.currentTimeMillis())
                val total = wordDao.getLeitnerTotalCount()
                val (title, message) = buildMessage(due, total)

                val tapIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pi = PendingIntent.getActivity(
                    context,
                    2001,
                    tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, LexoraApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()

                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                try {
                    nm.notify(2002, notification)
                } catch (_: SecurityException) {
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    
    private fun buildMessage(due: Int, total: Int): Pair<String, String> = when {
        due == 0 && total == 0 ->
            "🃏 Leitner is empty" to "Add words to Leitner and I'll remind you to review them."
        due == 0 ->
            "🎉 All caught up!" to "Every one of your $total Leitner words is reviewed. Great job!"
        due < 5 ->
            "Quick review: $due word${if (due == 1) "" else "s"}" to
                    "Just $due word${if (due == 1) "" else "s"} waiting — 2 minutes and you're done!"
        due < 20 ->
            "📚 $due words are due" to "A short 10-minute session clears your whole queue."
        else ->
            "🔥 $due words piled up!" to "Your Leitner queue is full. Let's clear it together!"
    }
}
