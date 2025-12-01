package com.voicecalc.calculator

import com.voicecalc.model.CalculationResult
import com.voicecalc.model.InputItem
import java.util.Stack

/**
 * Calculator engine that performs calculations with proper operator precedence
 */
class CalculatorEngine {

    /**
     * Calculate result from list of input items
     * Supports: +, -, ×, ÷ with proper precedence
     */
    fun calculate(items: List<InputItem>): CalculationResult? {
        if (items.isEmpty()) return null

        try {
            // Convert to expression string
            val expression = items.joinToString(" ") { it.value }

            // Parse and calculate
            val result = evaluateExpression(items)

            return CalculationResult(
                expression = expression,
                result = result
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun evaluateExpression(items: List<InputItem>): Double {
        // Convert items to tokens
        val tokens = mutableListOf<String>()
        for (item in items) {
            tokens.add(item.value)
        }

        // Handle operator precedence using two stacks
        val values = Stack<Double>()
        val operators = Stack<String>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            when {
                // Number
                isNumber(token) -> {
                    values.push(token.toDouble())
                }
                // Operator
                isOperator(token) -> {
                    // Process operators with higher or equal precedence
                    while (operators.isNotEmpty() &&
                           hasPrecedence(operators.peek(), token)) {
                        val b = values.pop()
                        val a = values.pop()
                        val op = operators.pop()
                        values.push(applyOperation(a, b, op))
                    }
                    operators.push(token)
                }
            }
            i++
        }

        // Process remaining operators
        while (operators.isNotEmpty()) {
            val b = values.pop()
            val a = values.pop()
            val op = operators.pop()
            values.push(applyOperation(a, b, op))
        }

        return values.pop()
    }

    private fun isNumber(token: String): Boolean {
        return token.toDoubleOrNull() != null
    }

    private fun isOperator(token: String): Boolean {
        return token in listOf("+", "-", "×", "÷")
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op1 == "(" || op1 == ")") return false
        if ((op1 == "×" || op1 == "÷") && (op2 == "+" || op2 == "-")) return true
        if ((op1 == "+" || op1 == "-") && (op2 == "×" || op2 == "÷")) return false
        return true
    }

    private fun applyOperation(a: Double, b: Double, operator: String): Double {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> {
                if (b == 0.0) throw ArithmeticException("Division by zero")
                a / b
            }
            else -> throw IllegalArgumentException("Unknown operator: $operator")
        }
    }
}
