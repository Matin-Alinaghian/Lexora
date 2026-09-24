package com.lexora.app.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

private const val TAG = "SoundManager"

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isSoundEnabled = true
    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        private val pcmCache = mutableMapOf<SoundType, ShortArray>()

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    enum class SoundType {
        CLICK, POPUP, SUCCESS, ERROR, CARD_FLIP, CARD_KNEW,
        CARD_DIDNT_KNOW, CELEBRATION, XP_GAINED, LEVEL_UP,
        QUIZ_COMPLETE, QUIZ_EXCELLENT, STREAK, CARD_REVEAL,
        QUIZ_TICK, QUIZ_TOCK, NAV_TAB
    }

    init {
                scope.launch {
            SoundType.entries.forEach { type ->
                pcmCache[type] = generatePcmForType(type)
            }
        }
    }

    
    fun playSound(type: SoundType) {
        if (!isSoundEnabled) return
        
        scope.launch {
            try {
                val pcmData = pcmCache[type] ?: generatePcmForType(type).also { pcmCache[type] = it }
                
                                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)                     .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                
                val format = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val bufferSize = pcmData.size * 2
                
                                val track = AudioTrack.Builder()
                    .setAudioAttributes(attributes)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmData, 0, pcmData.size)
                track.play()
                
                                val durationMs = (pcmData.size.toDouble() / sampleRate * 1000).toLong()
                delay(durationMs + 200)
                
                track.stop()
                track.release()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play sound $type", e)
            }
        }
    }

    private fun generatePcmForType(type: SoundType): ShortArray = when (type) {
        SoundType.CLICK -> generateTone(800.0, 0.05, 0.4)
        SoundType.POPUP -> generateSoftBubble(600.0, 0.2, 0.4)
        SoundType.NAV_TAB -> generateSoftBubble(450.0, 0.15, 0.35)
        SoundType.SUCCESS -> generateChime(listOf(523.25, 659.25, 783.99, 1046.50), 0.6)
        SoundType.ERROR -> generateTone(180.0, 0.3, 0.5, isDissonant = true)
        SoundType.CARD_FLIP -> generateSwoosh()
        SoundType.CARD_KNEW -> generateChime(listOf(659.25, 880.0), 0.2)
        SoundType.CARD_DIDNT_KNOW -> generateTone(220.0, 0.25, 0.4)
        SoundType.XP_GAINED -> generateTone(1200.0, 0.1, 0.3)
        SoundType.CELEBRATION -> generateCelebration()
        SoundType.LEVEL_UP -> generateChime(listOf(440.0, 554.37, 659.25, 880.0), 0.8)
        SoundType.QUIZ_COMPLETE -> generateChime(listOf(523.25, 659.25, 783.99, 1046.50), 0.5)
        SoundType.QUIZ_EXCELLENT -> generateFanfare()
        SoundType.STREAK -> generateChime(listOf(880.0, 1108.73, 1318.51), 0.3)
        SoundType.CARD_REVEAL -> generateReveal()
        SoundType.QUIZ_TICK -> generateTone(1500.0, 0.02, 0.2)
        SoundType.QUIZ_TOCK -> generateTone(1000.0, 0.02, 0.2)
    }

    private fun generateTone(freq: Double, duration: Double, volume: Double, isDissonant: Boolean = false): ShortArray {
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val envelope = when {
                t < 0.01 -> t / 0.01
                t > duration - 0.02 -> (duration - t) / 0.02
                else -> 1.0
            }
            var wave = sin(2.0 * PI * freq * t)
            if (isDissonant) wave = (wave + 0.6 * sin(2.0 * PI * freq * 1.41 * t)) / 1.6
            data[i] = (wave * envelope * volume * 32767.0).toInt().toShort()
        }
        return data
    }

    private fun generateSoftBubble(baseFreq: Double, duration: Double, volume: Double): ShortArray {
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
                        val freq = baseFreq + (t / duration) * (baseFreq * 1.5)
                        val envelope = exp(-18.0 * (t - 0.02).pow(2.0) / (duration * duration)) * 
                          exp(-10.0 * t)             
            data[i] = (sin(2.0 * PI * freq * t) * envelope * volume * 32767.0).toInt().toShort()
        }
        return data
    }

    private fun generateChime(freqs: List<Double>, duration: Double): ShortArray {
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        val partDuration = duration / freqs.size
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            var combinedWave = 0.0
            freqs.forEachIndexed { idx, f ->
                val noteStart = idx * partDuration * 0.8
                val noteAge = t - noteStart
                if (noteAge >= 0) {
                    val envelope = exp(-5.0 * noteAge)
                    combinedWave += sin(2.0 * PI * f * t) * envelope
                }
            }
            val globalEnvelope = if (t > duration - 0.05) (duration - t) / 0.05 else 1.0
            data[i] = (combinedWave * globalEnvelope * 0.4 * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return data
    }

    private fun generateSwoosh(): ShortArray {
        val duration = 0.2
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 300.0 + progress * 2000.0
            val envelope = sin(PI * progress) * 0.5
            data[i] = (sin(2.0 * PI * freq * t) * envelope * 0.4 * 32767.0).toInt().toShort()
        }
        return data
    }

    private fun generateReveal(): ShortArray {
        val duration = 0.3
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 400.0 + sin(progress * PI) * 400.0
            val envelope = sin(PI * progress) * 0.6
            data[i] = (sin(2.0 * PI * freq * t) * envelope * 0.4 * 32767.0).toInt().toShort()
        }
        return data
    }

    private fun generateCelebration(): ShortArray {
        val duration = 1.5
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        val freqs = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            var wave = 0.0
            freqs.forEachIndexed { idx, f ->
                val start = idx * 0.15
                val age = t - start
                if (age >= 0) {
                    wave += sin(2.0 * PI * f * t) * exp(-2.0 * age)
                }
            }
            val env = if (t > duration - 0.2) (duration - t) / 0.2 else 1.0
            data[i] = (wave * env * 0.3 * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return data
    }

    private fun generateFanfare(): ShortArray {
        val duration = 1.2
        val count = (sampleRate * duration).toInt()
        val data = ShortArray(count)
        val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            var wave = 0.0
            notes.forEachIndexed { idx, freq ->
                val start = idx * 0.1
                val age = t - start
                if (age >= 0) {
                    wave += sin(2.0 * PI * freq * t) * exp(-1.5 * age)
                }
            }
            val env = if (t > duration - 0.2) (duration - t) / 0.2 else 1.0
            data[i] = (wave * env * 0.4 * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return data
    }
}
