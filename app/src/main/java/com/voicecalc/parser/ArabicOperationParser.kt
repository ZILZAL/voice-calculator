package com.voicecalc.parser

import com.voicecalc.model.ArabicDialect

/**
 * Parser for mathematical operations in all Arabic dialects
 */
class ArabicOperationParser {

    companion object {
        const val OP_ADD = "+"
        const val OP_SUBTRACT = "-"
        const val OP_MULTIPLY = "×"
        const val OP_DIVIDE = "÷"
        const val OP_EQUALS = "="
        const val OP_CLEAR = "clear"
    }

    private val levantineOperations = mapOf(
        // Addition
        "زائد" to OP_ADD,
        "زايد" to OP_ADD,
        "جمع" to OP_ADD,
        "و" to OP_ADD,
        "مع" to OP_ADD,
        "plus" to OP_ADD,

        // Subtraction
        "ناقص" to OP_SUBTRACT,
        "طرح" to OP_SUBTRACT,
        "أقل" to OP_SUBTRACT,
        "اقل" to OP_SUBTRACT,
        "منه" to OP_SUBTRACT,
        "minus" to OP_SUBTRACT,

        // Multiplication
        "ضرب" to OP_MULTIPLY,
        "مضروب" to OP_MULTIPLY,
        "في" to OP_MULTIPLY,
        "مرة" to OP_MULTIPLY,
        "times" to OP_MULTIPLY,

        // Division
        "قسمة" to OP_DIVIDE,
        "مقسوم" to OP_DIVIDE,
        "على" to OP_DIVIDE,
        "تقسيم" to OP_DIVIDE,
        "divide" to OP_DIVIDE,

        // Equals
        "يساوي" to OP_EQUALS,
        "يسوي" to OP_EQUALS,
        "نتيجة" to OP_EQUALS,
        "المجموع" to OP_EQUALS,
        "الناتج" to OP_EQUALS,
        "equals" to OP_EQUALS,

        // Clear
        "مسح" to OP_CLEAR,
        "حذف" to OP_CLEAR,
        "إعادة" to OP_CLEAR,
        "اعادة" to OP_CLEAR,
        "من جديد" to OP_CLEAR,
        "clear" to OP_CLEAR
    )

    private val palestinianOperations = mapOf(
        // Addition
        "زائد" to OP_ADD,
        "زايد" to OP_ADD,
        "جمع" to OP_ADD,
        "و" to OP_ADD,
        "مع" to OP_ADD,

        // Subtraction
        "ناقص" to OP_SUBTRACT,
        "طرح" to OP_SUBTRACT,
        "منه" to OP_SUBTRACT,
        "اقل" to OP_SUBTRACT,

        // Multiplication
        "ضرب" to OP_MULTIPLY,
        "في" to OP_MULTIPLY,
        "مضروب" to OP_MULTIPLY,

        // Division
        "قسمة" to OP_DIVIDE,
        "تقسيم" to OP_DIVIDE,
        "على" to OP_DIVIDE,
        "مقسوم" to OP_DIVIDE,

        // Equals
        "يساوي" to OP_EQUALS,
        "يسوي" to OP_EQUALS,
        "النتيجة" to OP_EQUALS,
        "الحاصل" to OP_EQUALS,
        "المجموع" to OP_EQUALS,

        // Clear
        "امسح" to OP_CLEAR,
        "احذف" to OP_CLEAR,
        "من جديد" to OP_CLEAR,
        "صفر" to OP_CLEAR
    )

    private val israeliOperations = mapOf(
        // Addition
        "زائد" to OP_ADD,
        "زايد" to OP_ADD,
        "جمع" to OP_ADD,
        "و" to OP_ADD,

        // Subtraction
        "ناقص" to OP_SUBTRACT,
        "طرح" to OP_SUBTRACT,
        "منه" to OP_SUBTRACT,

        // Multiplication
        "ضرب" to OP_MULTIPLY,
        "في" to OP_MULTIPLY,
        "مرة" to OP_MULTIPLY,

        // Division
        "قسمة" to OP_DIVIDE,
        "على" to OP_DIVIDE,
        "تقسيم" to OP_DIVIDE,

        // Equals
        "يساوي" to OP_EQUALS,
        "يسوي" to OP_EQUALS,
        "نتيجة" to OP_EQUALS,
        "الناتج" to OP_EQUALS,

        // Clear
        "مسح" to OP_CLEAR,
        "احذف" to OP_CLEAR,
        "من جديد" to OP_CLEAR
    )

    fun parse(text: String, dialect: ArabicDialect): String? {
        val normalized = text.trim().lowercase()

        val operationMap = when (dialect) {
            ArabicDialect.LEVANTINE -> levantineOperations
            ArabicDialect.PALESTINIAN -> palestinianOperations
            ArabicDialect.ISRAELI -> israeliOperations
        }

        return operationMap[normalized]
    }

    fun isOperation(text: String, dialect: ArabicDialect): Boolean {
        return parse(text, dialect) != null
    }

    fun getOperationSymbol(text: String, dialect: ArabicDialect): String? {
        val operation = parse(text, dialect) ?: return null
        return if (operation == OP_CLEAR || operation == OP_EQUALS) null else operation
    }
}
