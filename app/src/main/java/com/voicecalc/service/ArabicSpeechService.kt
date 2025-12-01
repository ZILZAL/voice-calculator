package com.voicecalc.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.voicecalc.model.ArabicDialect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Service for Arabic speech recognition
 */
class ArabicSpeechService(
    private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognitionIntent: Intent? = null

    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentDialect: ArabicDialect = ArabicDialect.LEVANTINE
    private var shouldContinue = false

    init {
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    fun setDialect(dialect: ArabicDialect) {
        currentDialect = dialect
        updateRecognitionIntent()
    }

    private fun updateRecognitionIntent() {
        recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentDialect.locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }
    }

    fun startListening() {
        if (_isListening.value) return

        shouldContinue = true
        _isListening.value = true
        _error.value = null
        updateRecognitionIntent()
        startRecognitionInternal()
    }

    fun stopListening() {
        shouldContinue = false
        _isListening.value = false
        speechRecognizer?.stopListening()
    }

    private fun startRecognitionInternal() {
        if (!shouldContinue) return

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Ready to listen
            }

            override fun onBeginningOfSpeech() {
                // User started speaking
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Audio level changed
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }

            override fun onEndOfSpeech() {
                // User stopped speaking
            }

            override fun onError(error: Int) {
                val errorMessage = getErrorMessage(error)

                // Auto-restart on specific errors
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        // Restart listening
                        if (shouldContinue) {
                            android.os.Handler(android.os.Looper.getMainLooper())
                                .postDelayed({
                                    startRecognitionInternal()
                                }, 100)
                        }
                    }
                    else -> {
                        _error.value = errorMessage
                        if (shouldContinue) {
                            // Retry after a short delay
                            android.os.Handler(android.os.Looper.getMainLooper())
                                .postDelayed({
                                    startRecognitionInternal()
                                }, 500)
                        }
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _recognizedText.value = matches[0]
                }

                // Auto-restart for continuous listening
                if (shouldContinue) {
                    android.os.Handler(android.os.Looper.getMainLooper())
                        .postDelayed({
                            startRecognitionInternal()
                        }, 100)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )
                if (!matches.isNullOrEmpty()) {
                    // Can show partial results in UI if needed
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Custom events
            }
        })

        speechRecognizer?.startListening(recognitionIntent)
    }

    private fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "خطأ في الصوت"
            SpeechRecognizer.ERROR_CLIENT -> "خطأ في العميل"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "الأذونات غير كافية"
            SpeechRecognizer.ERROR_NETWORK -> "خطأ في الشبكة"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "انتهت مهلة الشبكة"
            SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على الكلام"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "المحرك مشغول"
            SpeechRecognizer.ERROR_SERVER -> "خطأ في الخادم"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "لم يتم اكتشاف كلام"
            else -> "خطأ غير معروف"
        }
    }

    fun clearRecognizedText() {
        _recognizedText.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun destroy() {
        shouldContinue = false
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    companion object {
        fun isAvailable(context: Context): Boolean {
            return SpeechRecognizer.isRecognitionAvailable(context)
        }
    }
}
