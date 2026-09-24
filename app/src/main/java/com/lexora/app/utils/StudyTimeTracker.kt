package com.lexora.app.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lexora.app.data.repository.StudySessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyTimeTracker @Inject constructor(
    private val studySessionRepository: StudySessionRepository,
    private val liveStudyTimer: LiveStudyTimer
) : DefaultLifecycleObserver {

    private var startTime: Long = 0
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStart(owner: LifecycleOwner) {
        startTime = System.currentTimeMillis()
    }

    override fun onStop(owner: LifecycleOwner) {
                val elapsedMinutes = liveStudyTimer.getMinutesAndReset()
        val wallMinutes = if (startTime > 0) {
            val endTime = System.currentTimeMillis()
            ((endTime - startTime) / (1000 * 60)).toInt()
        } else 0

        val durationMinutes = maxOf(elapsedMinutes, wallMinutes)
        if (durationMinutes >= 1) {
            scope.launch {
                studySessionRepository.recordStudyTime(durationMinutes)
            }
        }
        startTime = 0
    }
}
