package com.onyxera.fs.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Sesli Yazdırma (Voice-to-Text) Modülü.
 * Saha personelinin eldivenle veya zorlu koşullarda veri girişini hızlandırmak için
 * Android SpeechRecognizer API'sini Solnod operasyon standartlarında entegre eder.
 */
class SpeechHelper(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }

    /**
     * Mikrofonu dinlemeye başlar ve sonucu callback üzerinden UI katmanına iletir.
     * @param onResult Dinleme başarılı olduğunda tanınan metni döndürür.
     * @param onError Hata veya iptal durumunda tetiklenir.
     */
    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Ses hatası."
                    SpeechRecognizer.ERROR_CLIENT -> "İstemci hatası."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon izni gerekli."
                    SpeechRecognizer.ERROR_NETWORK -> "Ağ bağlantısı koptu."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Ağ zaman aşımı."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Ses anlaşılamadı."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Sistem meşgul."
                    SpeechRecognizer.ERROR_SERVER -> "Sunucu hatası."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Konuşma algılanmadı."
                    else -> "Bilinmeyen bir hata oluştu."
                }
                onError(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                } else {
                    onError("Ses anlaşılamadı.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
    }
}