package com.voicecalc.calculator

import com.voicecalc.model.CalculationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages calculation history
 */
class CalculationHistory {

    private val _history = MutableStateFlow<List<CalculationResult>>(emptyList())
    val history: StateFlow<List<CalculationResult>> = _history.asStateFlow()

    fun addResult(result: CalculationResult) {
        val currentHistory = _history.value.toMutableList()
        currentHistory.add(0, result) // Add to beginning
        // Keep only last 20 results
        if (currentHistory.size > 20) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _history.value = currentHistory
    }

    fun clear() {
        _history.value = emptyList()
    }

    fun getLatest(): CalculationResult? {
        return _history.value.firstOrNull()
    }
}
