package com.voicecalc.parser

/**
 * Parser for Palestinian Arabic dialect numbers
 */
class PalestinianParser {

    private val basicNumbers = mapOf(
        "صفر" to 0,
        "واحد" to 1,
        "اتنين" to 2,
        "اثنين" to 2,
        "تلاتة" to 3,
        "ثلاثة" to 3,
        "أربعة" to 4,
        "اربعة" to 4,
        "أربع" to 4,
        "خمسة" to 5,
        "ستة" to 6,
        "سبعة" to 7,
        "تمانية" to 8,
        "ثمانية" to 8,
        "تسعة" to 9,
        "عشرة" to 10,
        "عشره" to 10
    )

    private val teens = mapOf(
        "حداش" to 11,
        "احدعش" to 11,
        "إحدعش" to 11,
        "اطناش" to 12,
        "اثنعش" to 12,
        "اثناش" to 12,
        "تلطعش" to 13,
        "تلتعش" to 13,
        "أربعطعش" to 14,
        "اربعطعش" to 14,
        "خمسطعش" to 15,
        "خمستعش" to 15,
        "ستطعش" to 16,
        "سبعطعش" to 17,
        "تمنطعش" to 18,
        "تسعطعش" to 19
    )

    private val tens = mapOf(
        "عشرين" to 20,
        "عشرون" to 20,
        "تلاتين" to 30,
        "ثلاثين" to 30,
        "أربعين" to 40,
        "اربعين" to 40,
        "خمسين" to 50,
        "ستين" to 60,
        "سبعين" to 70,
        "تمانين" to 80,
        "ثمانين" to 80,
        "تسعين" to 90
    )

    private val hundreds = mapOf(
        "مية" to 100,
        "مئة" to 100,
        "ميه" to 100,
        "ميتين" to 200,
        "مئتين" to 200,
        "مئتان" to 200,
        "تلتمية" to 300,
        "تلاتمية" to 300,
        "ثلاثمائة" to 300,
        "أربعمية" to 400,
        "اربعمية" to 400,
        "أربعمائة" to 400,
        "خمسمية" to 500,
        "خمسمائة" to 500,
        "ستمية" to 600,
        "ستمائة" to 600,
        "سبعمية" to 700,
        "سبعمائة" to 700,
        "تمنمية" to 800,
        "ثمانمائة" to 800,
        "تسعمية" to 900,
        "تسعمائة" to 900
    )

    private val thousands = mapOf(
        "ألف" to 1000,
        "الف" to 1000,
        "ألفين" to 2000,
        "الفين" to 2000,
        "ألفان" to 2000
    )

    fun parse(text: String): Double? {
        val normalized = text.trim().lowercase()

        // Try direct match first
        basicNumbers[normalized]?.let { return it.toDouble() }
        teens[normalized]?.let { return it.toDouble() }
        tens[normalized]?.let { return it.toDouble() }
        hundreds[normalized]?.let { return it.toDouble() }
        thousands[normalized]?.let { return it.toDouble() }

        // Try parsing as a number string
        normalized.toDoubleOrNull()?.let { return it }

        // Try compound numbers
        return parseCompound(normalized)
    }

    private fun parseCompound(text: String): Double? {
        val cleaned = text.replace("و", " ")
            .replace("  ", " ")
            .trim()

        val parts = cleaned.split(" ")
        var total = 0.0
        var currentMultiplier = 1

        for (part in parts.reversed()) {
            val value = parse(part) ?: continue

            when {
                value >= 1000 -> {
                    currentMultiplier = value.toInt()
                }
                value >= 100 -> {
                    total += value * currentMultiplier
                }
                else -> {
                    total += value
                }
            }
        }

        return if (total > 0) total else null
    }
}
