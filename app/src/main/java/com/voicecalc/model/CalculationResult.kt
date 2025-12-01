package com.voicecalc.model

/**
 * Represents the result of a calculation
 */
data class CalculationResult(
    val expression: String,
    val result: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedResult(): String {
        return if (result % 1.0 == 0.0) {
            result.toInt().toString()
        } else {
            String.format("%.2f", result)
        }
    }

    override fun toString(): String = "$expression = ${getFormattedResult()}"
}
