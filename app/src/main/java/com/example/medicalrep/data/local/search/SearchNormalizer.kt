package com.example.medicalrep.data.local.search

import java.util.Locale

private val ALEF_FORMS = setOf('أ', 'إ', 'آ', 'ٱ')
private const val ALEF = 'ا'
private const val ALEF_MAQSURA = 'ى'
private const val YA = 'ي'
private const val TA_MARBUTA = 'ة'
private const val HA = 'ه'
private const val TATWEEL = 'ـ'
private val DIACRITICS = '\u064B'..'\u065F'
private const val SUPERSCRIPT_ALEF = '\u0670'
private val ARABIC_INDIC_DIGITS = '\u0660'..'\u0669'
private val EXTENDED_ARABIC_INDIC_DIGITS = '\u06F0'..'\u06F9'

/**
 * Makes Arabic and English text comparable: lowercase, one alef, ya for alef maqsura, ha for ta marbuta,
 * no diacritics or tatweel, ASCII digits, single spaces. Search boxes and stored `searchText` both use it.
 */
fun normalizeSearch(text: String): String {
    val normalized = buildString(text.length) {
        for (char in text.lowercase(Locale.ROOT)) {
            when {
                char in DIACRITICS || char == SUPERSCRIPT_ALEF || char == TATWEEL -> Unit
                char in ALEF_FORMS -> append(ALEF)
                char == ALEF_MAQSURA -> append(YA)
                char == TA_MARBUTA -> append(HA)
                char in ARABIC_INDIC_DIGITS -> append('0' + (char - ARABIC_INDIC_DIGITS.first))
                char in EXTENDED_ARABIC_INDIC_DIGITS -> append('0' + (char - EXTENDED_ARABIC_INDIC_DIGITS.first))
                else -> append(char)
            }
        }
    }
    return normalized.trim().replace(Regex("\\s+"), " ")
}

/** The text a row is searched by: its searchable fields normalized and joined; null and blank parts are skipped. */
fun searchTextOf(vararg parts: String?): String =
    parts.mapNotNull { it?.let(::normalizeSearch)?.takeIf(String::isNotEmpty) }.joinToString(" ")
