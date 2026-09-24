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
import com.lexora.app.data.local.dao.GrammarDao
import com.lexora.app.data.local.dao.NoteDao
import com.lexora.app.data.local.dao.WordDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReviewDueReceiver : BroadcastReceiver() {

    @Inject lateinit var wordDao: WordDao
    @Inject lateinit var grammarDao: GrammarDao
    @Inject lateinit var noteDao: NoteDao
    @Inject lateinit var reviewScheduleManager: ReviewScheduleManager

    companion object {
        const val ACTION = "com.lexora.app.REVIEW_DUE_ALARM"
        private const val PREFS = "lexora_review_due"
        private const val KEY_ENABLED = "enabled"
        const val REQUEST_CODE = 4001
        const val NOTIFICATION_ID = 4002

        fun isDueReminderEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENABLED, true)
        }

        fun setDueReminderEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ENABLED, enabled).apply()
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION) return
        if (!isDueReminderEnabled(context)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                reviewScheduleManager.refreshNow()

                val now = System.currentTimeMillis()
                val dueWords = wordDao.getLeitnerDueCount(now)
                val dueGrammar = grammarDao.getLeitnerGrammarDueCount(now)
                val dueNotes = noteDao.getLeitnerNotesDueCount(now)
                val dueTotal = dueWords + dueGrammar + dueNotes

                if (dueTotal == 0) {
                    ReviewDueScheduler.scheduleNextCheck(context)
                    return@launch
                }

                val (title, message) = buildMessage(dueTotal, dueWords, dueGrammar, dueNotes)

                val tapIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pi = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, LexoraApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()

                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                try {
                    nm.notify(NOTIFICATION_ID, notification)
                } catch (_: SecurityException) {
                }

                ReviewDueScheduler.scheduleNextCheck(context)
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun buildMessage(total: Int, words: Int, grammar: Int, notes: Int): Pair<String, String> {
        val parts = mutableListOf<String>()
        if (words > 0) parts.add("$words word${if (words == 1) "" else "s"}")
        if (grammar > 0) parts.add("$grammar grammar")
        if (notes > 0) parts.add("$notes note${if (notes == 1) "" else "s"}")
        val breakdown = parts.joinToString(", ")
        return when {
            total < 5 ->
                "Quick review: $total item${if (total == 1) "" else "s"}" to
                    "Just $breakdown waiting \u2014 2 minutes and you're done!"
            total < 20 ->
                "$total cards are due" to
                    "A short 10-minute session clears your whole queue."
            else ->
                "$total cards piled up!" to
                    "Your review queue is full: $breakdown. Let's clear it together!"
        }
    }
}
