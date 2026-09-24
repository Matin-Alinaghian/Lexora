package com.lexora.app.notification

import android.content.Context
import com.lexora.app.data.repository.GrammarRepository
import com.lexora.app.data.repository.NoteRepository
import com.lexora.app.data.repository.WordRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewScheduleManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordRepository: WordRepository,
    private val grammarRepository: GrammarRepository,
    private val noteRepository: NoteRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun refresh() {
        scope.launch { refreshNow() }
    }

    suspend fun refreshNow() {
        try {
            val now = System.currentTimeMillis()
            advanceStale(now)
            nextRelevantTime(now)?.let { ReviewDueScheduler.updateEarliestDueTime(context, it) }
        } catch (_: Exception) {
        }
    }

    private suspend fun advanceStale(now: Long) {
        wordRepository.getAllLeitnerWordsOnce().forEach { word ->
            if (isStale(word.nextReviewDate, now)) {
                val target = scheduleAfter(word.createdAt, now)
                wordRepository.updateWord(
                    word.copy(
                        leitnerBox = target.box,
                        nextReviewDate = target.nextReview,
                        updatedAt = now
                    )
                )
            }
        }

        grammarRepository.getAllGrammar().first()
            .filter { it.isInLeitner && isStale(it.nextReviewDate, now) }
            .forEach { grammar ->
                val target = scheduleAfter(grammar.createdAt, now)
                grammarRepository.updateGrammar(
                    grammar.copy(
                        leitnerBox = target.box,
                        nextReviewDate = target.nextReview,
                        updatedAt = now
                    )
                )
            }

        noteRepository.getAllNotes().first()
            .filter { it.isInLeitner && isStale(it.nextReviewDate, now) }
            .forEach { note ->
                val target = scheduleAfter(note.createdAt, now)
                noteRepository.updateNote(
                    note.copy(
                        leitnerBox = target.box,
                        nextReviewDate = target.nextReview,
                        updatedAt = now
                    )
                )
            }
    }

    private suspend fun nextRelevantTime(now: Long): Long? {
        val dueTimes = mutableListOf<Long>()

        wordRepository.getAllLeitnerWordsOnce()
            .forEach { if (it.nextReviewDate > 0) dueTimes.add(it.nextReviewDate) }

        grammarRepository.getAllGrammar().first()
            .filter { it.isInLeitner && it.nextReviewDate > 0 }
            .forEach { dueTimes.add(it.nextReviewDate) }

        noteRepository.getAllNotes().first()
            .filter { it.isInLeitner && it.nextReviewDate > 0 }
            .forEach { dueTimes.add(it.nextReviewDate) }

        val earliest = dueTimes.minOrNull() ?: return null
        val relevant = if (earliest > now) earliest else earliest + OVERDUE_GRACE_MS
        return maxOf(relevant, now + MIN_RESCHEDULE_DELAY_MS)
    }

    private fun isStale(nextReviewDate: Long, now: Long): Boolean =
        nextReviewDate <= 0 || nextReviewDate + OVERDUE_GRACE_MS <= now

    private fun scheduleAfter(createdAt: Long, now: Long): ScheduleTarget {
        val elapsedDays = ((now - createdAt).coerceAtLeast(0L)) / DAY_MS
        var index = 1
        while (boundaryDays(index) <= elapsedDays && index < MAX_BOUNDARY_INDEX) index++
        return ScheduleTarget(
            box = index.coerceAtMost(MAX_BOX),
            nextReview = createdAt + boundaryDays(index) * DAY_MS
        )
    }

    private fun boundaryDays(index: Int): Long =
        if (index <= BOUNDARY_DAYS.size) BOUNDARY_DAYS[index - 1]
        else BOUNDARY_DAYS.last() + (index - BOUNDARY_DAYS.size) * 30L

    private data class ScheduleTarget(val box: Int, val nextReview: Long)

    companion object {
        private const val DAY_MS = 24L * 60 * 60 * 1000
        private const val MAX_BOX = 5
        private const val MAX_BOUNDARY_INDEX = 400

        private const val OVERDUE_GRACE_MS = DAY_MS
        private const val MIN_RESCHEDULE_DELAY_MS = 30L * 60 * 1000

        private val BOUNDARY_DAYS = longArrayOf(1L, 4L, 11L, 25L, 55L)
    }
}
