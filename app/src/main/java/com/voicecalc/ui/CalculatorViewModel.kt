package com.voicecalc.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicecalc.calculator.CalculationHistory
import com.voicecalc.calculator.CalculatorEngine
import com.voicecalc.calculator.EditableInputManager
import com.voicecalc.model.ArabicDialect
import com.voicecalc.model.CalculationResult
import com.voicecalc.model.InputItem
import com.voicecalc.parser.VoiceInputProcessor
import com.voicecalc.service.ArabicSpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val speechService = ArabicSpeechService(application)
    private val inputManager = EditableInputManager()
    private val calculatorEngine = CalculatorEngine()
    private val history = CalculationHistory()

    private var voiceProcessor: VoiceInputProcessor = VoiceInputProcessor(ArabicDialect.LEVANTINE)

    private val _currentDialect = MutableStateFlow(ArabicDialect.LEVANTINE)
    val currentDialect: StateFlow<ArabicDialect> = _currentDialect.asStateFlow()

    private val _lastHeardText = MutableStateFlow<String?>(null)
    val lastHeardText: StateFlow<String?> = _lastHeardText.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult: StateFlow<CalculationResult?> = _calculationResult.asStateFlow()

    val inputList: StateFlow<List<InputItem>> = inputManager.inputList
    val isListening: StateFlow<Boolean> = speechService.isListening
    val error: StateFlow<String?> = speechService.error
    val historyList: StateFlow<List<CalculationResult>> = history.history

    init {
        observeSpeechRecognition()
    }

    private fun observeSpeechRecognition() {
        viewModelScope.launch {
            speechService.recognizedText.collect { text ->
                if (text != null) {
                    processVoiceInput(text)
                    speechService.clearRecognizedText()
                }
            }
        }
    }

    private fun processVoiceInput(text: String) {
        _lastHeardText.value = text

        // Check if it's a command
        val command = voiceProcessor.isCommand(text)
        when (command) {
            "clear" -> clearAll()
            "equals" -> calculateResult()
            else -> {
                // Try to process as number or operation
                val item = voiceProcessor.processInput(text)
                if (item != null) {
                    inputManager.addItem(item)
                }
            }
        }
    }

    fun setDialect(dialect: ArabicDialect) {
        _currentDialect.value = dialect
        speechService.setDialect(dialect)
        voiceProcessor = VoiceInputProcessor(dialect)
    }

    fun startListening() {
        speechService.startListening()
    }

    fun stopListening() {
        speechService.stopListening()
    }

    fun calculateResult() {
        val items = inputManager.getItems()
        if (items.isEmpty()) return

        val result = calculatorEngine.calculate(items)
        if (result != null) {
            _calculationResult.value = result
            history.addResult(result)
        }
    }

    fun updateInputItem(id: String, newValue: String) {
        inputManager.updateItem(id, newValue)
    }

    fun deleteInputItem(id: String) {
        inputManager.deleteItem(id)
    }

    fun clearAll() {
        inputManager.clear()
        _calculationResult.value = null
        _lastHeardText.value = null
    }

    fun clearHistory() {
        history.clear()
    }

    fun clearError() {
        speechService.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        speechService.destroy()
    }

    companion object {
        fun isSpeechRecognitionAvailable(application: Application): Boolean {
            return ArabicSpeechService.isAvailable(application)
        }
    }
}
