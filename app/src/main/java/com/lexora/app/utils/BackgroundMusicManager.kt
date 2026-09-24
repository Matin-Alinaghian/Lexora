package com.lexora.app.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

private const val TAG = "BackgroundMusic"

@Singleton
class BackgroundMusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var audioTrack: AudioTrack? = null
    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.35f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _musicEnabled = MutableStateFlow(true)
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    init {
            }

    private fun pianoWave(t: Double, freq: Double, velocity: Double): Double {
        var wave = 0.0
        wave += 1.0 * sin(2.0 * PI * freq * t)
        wave += 0.5 * sin(2.0 * PI * freq * 2.0 * t)
        wave += 0.25 * sin(2.0 * PI * freq * 3.0 * t)
        wave += 0.12 * sin(2.0 * PI * freq * 4.0 * t)
        wave += 0.08 * sin(2.0 * PI * freq * 2.003 * t)
        return wave * velocity
    }

    private fun generateChordBlock(
        bass: Double, notes: List<Double>, velocities: List<Double>,
        sampleRate: Int, duration: Double
    ): ShortArray {
        val totalSamples = (sampleRate * duration).toInt()
        val data = ShortArray(totalSamples)
        val noteSpacing = duration / notes.size

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate

            var wave = 0.0

                        val bassEnv = exp(-0.4 * t)
            wave += pianoWave(t, bass, 0.15) * bassEnv

                        notes.forEachIndexed { idx, freq ->
                val noteStart = idx * noteSpacing
                val noteAge = t - noteStart
                if (noteAge >= 0) {
                                        val envelope = when {
                        noteAge < 0.008 -> noteAge / 0.008
                        noteAge < 0.008 + 0.6 -> {
                            val p = (noteAge - 0.008) / 0.6
                            1.0 - 0.7 * p
                        }
                        else -> {
                            val sa = noteAge - 0.008 - 0.6
                            0.3 * exp(-0.8 * sa)
                        }
                    }
                    wave += pianoWave(noteAge, freq, velocities[idx]) * envelope
                }
            }

            data[i] = (wave * 0.5 * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return data
    }

    private fun startStreaming() {
        if (musicJob?.isActive == true) return

        try {
            val sampleRate = 44100
            val chordDuration = 4.0
            val bufferSize = sampleRate * 2  
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.setVolume(_volume.value)
            audioTrack?.play()

                        val chords = listOf(
                Triple(65.41, listOf(261.63, 329.63, 392.00, 493.88, 329.63, 261.63),
                    listOf(0.30, 0.25, 0.22, 0.18, 0.15, 0.12)),
                Triple(55.00, listOf(220.00, 261.63, 329.63, 392.00, 329.63, 220.00),
                    listOf(0.28, 0.22, 0.20, 0.16, 0.13, 0.10)),
                Triple(43.65, listOf(174.61, 220.00, 261.63, 329.63, 261.63, 174.61),
                    listOf(0.30, 0.24, 0.20, 0.18, 0.14, 0.11)),
                Triple(49.00, listOf(196.00, 246.94, 293.66, 349.23, 293.66, 196.00),
                    listOf(0.28, 0.22, 0.20, 0.16, 0.13, 0.10))
            )

            musicJob = scope.launch {
                try {
                    while (isActive) {
                        for ((bass, notes, velocities) in chords) {
                            if (!isActive) break
                            val block = generateChordBlock(bass, notes, velocities, sampleRate, chordDuration)
                            var offset = 0
                            val chunkSize = sampleRate
                            while (offset < block.size && isActive) {
                                val remaining = block.size - offset
                                val toWrite = minOf(chunkSize, remaining)
                                val written = audioTrack?.write(block, offset, toWrite) ?: 0
                                if (written < 0) break
                                offset += written
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    return@launch
                } catch (e: Exception) {
                    Log.e(TAG, "Music streaming error", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start music", e)
        }
    }

    fun toggleMusic() {
        if (_isPlaying.value) {
            pauseMusic()
        } else {
            startMusic()
        }
    }

    fun startMusic() {
        if (!_musicEnabled.value) return
        if (_isPlaying.value) return
        _isPlaying.value = true
        startStreaming()
    }

    fun pauseMusic() {
        _isPlaying.value = false
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.pause()
        } catch (_: Exception) {}
    }

    fun stopMusic() {
        _isPlaying.value = false
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    fun setMusicVolume(vol: Float) {
        val coerced = vol.coerceIn(0.0f, 1.0f)
        _volume.value = coerced
        try {
            audioTrack?.setVolume(coerced)
        } catch (_: Exception) {}
    }

    fun setMusicEnabled(enabled: Boolean) {
        _musicEnabled.value = enabled
        if (!enabled) {
            pauseMusic()
        } else {
            startMusic()
        }
    }
}

val LocalMusicManager = staticCompositionLocalOf<BackgroundMusicManager> {
    error("No MusicManager provided")
}
