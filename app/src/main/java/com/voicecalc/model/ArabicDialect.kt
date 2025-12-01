package com.voicecalc.model

/**
 * Represents the three supported Arabic dialects
 */
enum class ArabicDialect(val displayName: String, val locale: String) {
    LEVANTINE("شامي (Levantine)", "ar-LB"),
    PALESTINIAN("فلسطيني (Palestinian)", "ar-PS"),
    ISRAELI("عربي إسرائيلي (Israeli Arabic)", "ar-IL");

    companion object {
        fun fromLocale(locale: String): ArabicDialect {
            return values().find { it.locale == locale } ?: LEVANTINE
        }
    }
}
