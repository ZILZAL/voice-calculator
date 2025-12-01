package com.voicecalc.parser

import com.voicecalc.model.ArabicDialect
import com.voicecalc.model.InputItem

/**
 * Main processor that coordinates number and operation parsing
 */
class VoiceInputProcessor(private val dialect: ArabicDialect) {

    private val levantineParser = LevantineParser()
    private val palestinianParser = PalestinianParser()
    private val israeliParser = IsraeliArabicParser()
    private val operationParser = ArabicOperationParser()

    fun processInput(text: String): InputItem? {
        if (text.isBlank()) return null

        // First check if it's an operation
        val operation = operationParser.parse(text, dialect)
        if (operation != null) {
            return when (operation) {
                ArabicOperationParser.OP_CLEAR -> null // Handle clear separately
                ArabicOperationParser.OP_EQUALS -> null // Handle equals separately
                else -> InputItem(
                    value = operation,
                    type = InputItem.ItemType.OPERATION
                )
            }
        }

        // Try to parse as a number
        val number = parseNumber(text)
        if (number != null) {
            return InputItem(
                value = formatNumber(number),
                type = InputItem.ItemType.NUMBER
            )
        }

        return null
    }

    private fun parseNumber(text: String): Double? {
        return when (dialect) {
            ArabicDialect.LEVANTINE -> levantineParser.parse(text)
            ArabicDialect.PALESTINIAN -> palestinianParser.parse(text)
            ArabicDialect.ISRAELI -> israeliParser.parse(text)
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number % 1.0 == 0.0) {
            number.toInt().toString()
        } else {
            number.toString()
        }
    }

    fun isCommand(text: String): String? {
        val normalized = text.trim().lowercase()
        val operation = operationParser.parse(normalized, dialect)

        return when (operation) {
            ArabicOperationParser.OP_CLEAR -> "clear"
            ArabicOperationParser.OP_EQUALS -> "equals"
            else -> null
        }
    }
}
