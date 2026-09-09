package com.mohmb142.sam.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceEngine(context: Context) {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(appContext)

    fun listen(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!isAvailable()) { onError("التعرف على الكلام غير متاح على هذا الجهاز"); return }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).also { r ->
            r.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(p: android.os.Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(b: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(b: android.os.Bundle?) {}
                override fun onEvent(t: Int, b: android.os.Bundle?) {}
                override fun onError(e: Int) = onError("تعذر فهم الكلام (رمز $e)")
                override fun onResults(b: android.os.Bundle?) {
                    val text = b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (text.isNullOrBlank()) onError("لم أسمع كلاماً واضحاً") else onResult(text)
                }
            })
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            }
            r.startListening(intent)
        }
    }

    fun speak(text: String) {
        if (tts == null) {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ar")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sam_response")
            }
        } else tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sam_response")
    }

    fun release() {
        recognizer?.destroy(); recognizer = null
        tts?.stop(); tts?.shutdown(); tts = null
    }
}
