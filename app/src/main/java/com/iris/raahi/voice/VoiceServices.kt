package com.iris.raahi.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class RAAHIVoiceEngine(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private val recognizer = if (SpeechRecognizer.isRecognitionAvailable(context)) {
        SpeechRecognizer.createSpeechRecognizer(context)
    } else null
    var onReady: ((Boolean) -> Unit)? = null
    var onResult: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onListeningChanged: ((Boolean) -> Unit)? = null

    override fun onInit(status: Int) {
        val ready = status == TextToSpeech.SUCCESS
        if (ready) tts.language = Locale.getDefault()
        onReady?.invoke(ready)
    }

    fun speak(text: String, rate: Float = 1f, volume: Float = 1f) {
        tts.setSpeechRate(rate.coerceIn(0.5f, 2f))
        val params = android.os.Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.coerceIn(0f, 1f))
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "raahi")
    }

    fun stop() {
        tts.stop()
        recognizer?.cancel()
        onListeningChanged?.invoke(false)
    }

    fun listen(context: Context) {
        if (recognizer == null) {
            onError?.invoke("Voice recognition is not available on this device.")
            return
        }
        onListeningChanged?.invoke(true)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                onListeningChanged?.invoke(false)
                onError?.invoke(recognitionError(error))
            }
            override fun onResults(results: Bundle) {
                onListeningChanged?.invoke(false)
                val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!text.isNullOrBlank()) onResult?.invoke(text) else onError?.invoke("I did not hear a command.")
            }
            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        recognizer.startListening(intent)
    }

    fun shutdown() {
        recognizer?.destroy()
        tts.shutdown()
    }

    private fun recognitionError(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice recognition network is unavailable."
        SpeechRecognizer.ERROR_NO_MATCH -> "I did not recognize that command."
        SpeechRecognizer.ERROR_AUDIO -> "The microphone could not start."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
        else -> "Voice recognition is unavailable."
    }
}

object VoiceCommandParser {
    fun parse(command: String): VoiceCommand {
        val text = command.lowercase(Locale.ROOT)
        return when {
            text.contains("stop") -> VoiceCommand.Stop
            text.contains("repeat") -> VoiceCommand.Repeat
            text.contains("emergency") -> VoiceCommand.Emergency
            text.contains("navigation") || text.contains("navigate") -> VoiceCommand.Navigation
            text.contains("read") || text.contains("text") || text.contains("ocr") -> VoiceCommand.OCR
            text.contains("describe") || text.contains("surround") -> VoiceCommand.Describe
            else -> VoiceCommand.Unknown(command)
        }
    }
}

sealed interface VoiceCommand {
    data object Navigation : VoiceCommand
    data object OCR : VoiceCommand
    data object Describe : VoiceCommand
    data object Stop : VoiceCommand
    data object Repeat : VoiceCommand
    data object Emergency : VoiceCommand
    data class Unknown(val text: String) : VoiceCommand
}
