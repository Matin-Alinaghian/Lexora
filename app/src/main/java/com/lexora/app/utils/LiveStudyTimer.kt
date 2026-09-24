package com.lexora.app.utils

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lexora.app.data.repository.StreakRepository
import com.lexora.app.data.repository.StudySessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveStudyTimer @Inject constructor(
    private val streakRepository: StreakRepository,
    private val studySessionRepository: StudySessionRepository
) : DefaultLifecycleObserver {

    private var timer: Timer? = null
    private var totalSeconds = 0
    private var hasRegisteredToday = false
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _elapsedMinutes = MutableStateFlow(0)
    val elapsedMinutes: StateFlow<Int> = _elapsedMinutes.asStateFlow()

    override fun onStart(owner: LifecycleOwner) {
                startTimer()
                if (!hasRegisteredToday) {
            hasRegisteredToday = true
            scope.launch {
                streakRepository.addStudySession(
                    studyTimeMinutes = 0,
                    wordsReviewed = 0,
                    quizzesCompleted = 0,
                    correctAnswers = 0,
                    wrongAnswers = 0
                )
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
                stopTimer()
        persistTimeToDb()
    }

    private fun persistTimeToDb() {
        val minutes = totalSeconds / 60
        if (minutes > 0) {
            scope.launch {
                studySessionRepository.recordStudyTime(minutes)
            }
        }
        totalSeconds = 0
        _elapsedMinutes.value = 0
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    totalSeconds += 60
                    val minutes = totalSeconds / 60
                    _elapsedMinutes.value = minutes
                                        scope.launch {
                        studySessionRepository.recordStudyTime(1)
                    }
                    totalSeconds = 0
                }
            }, 60_000, 60_000)         }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    fun getMinutesAndReset(): Int {
        val minutes = totalSeconds / 60
        totalSeconds = 0
        _elapsedMinutes.value = 0
        return minutes
    }
}
