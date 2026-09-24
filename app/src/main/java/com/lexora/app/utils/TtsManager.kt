package com.lexora.app.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor() {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    
    fun init(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context) { status ->
                isInitialized = status == TextToSpeech.SUCCESS
                if (isInitialized) {
                    tts?.language = Locale.US
                }
            }
        }
    }

    
    fun speak(text: String) {
        if (tts == null || !isInitialized) {
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    
    suspend fun speakAsync(text: String): Boolean = suspendCancellableCoroutine { continuation ->
        if (tts == null || !isInitialized) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val utteranceId = "lexora_${System.currentTimeMillis()}"
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) ?: TextToSpeech.ERROR
        val hasStarted = result == TextToSpeech.SUCCESS

        if (!hasStarted) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                continuation.resume(true)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                continuation.resume(false)
            }
            override fun onError(utteranceId: String?, errorCode: Int) {
                continuation.resume(false)
            }
        })
    }

    
    fun stop() {
        tts?.stop()
    }

    
    fun isReady(): Boolean = isInitialized && tts != null

    
    fun release() {
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
