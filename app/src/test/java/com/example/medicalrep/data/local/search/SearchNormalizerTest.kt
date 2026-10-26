package com.example.medicalrep.data.local.search

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 1. English is lowercased; 2. alef forms become plain alef; 3. alef maqsura becomes ya; 4. ta marbuta becomes ha;
 * 5. diacritics and tatweel are removed; 6. Arabic-Indic digits become ASCII digits;
 * 7. spaces are trimmed and collapsed; 8. empty / blank input gives an empty text;
 * 9. searchTextOf joins the normalized parts and skips null or blank ones.
 */
class SearchNormalizerTest {

    @Test
    fun `english is lowercased`() = assertEquals("al-iman pharmacy", normalizeSearch("Al-Iman PHARMACY"))

    @Test
    fun `alef forms become plain alef`() = assertEquals("احمد", normalizeSearch("أحمد"))

    @Test
    fun `all alef variants match`() {
        assertEquals("ا ا ا ا", normalizeSearch("أ إ آ ٱ"))
    }

    @Test
    fun `alef maqsura becomes ya`() = assertEquals("مستشفي", normalizeSearch("مستشفى"))

    @Test
    fun `ta marbuta becomes ha`() = assertEquals("مدرسه", normalizeSearch("مدرسة"))

    @Test
    fun `diacritics and tatweel are removed`() {
        assertEquals("محمد", normalizeSearch("مُحَمَّد"))
        assertEquals("محمد", normalizeSearch("مـحـمـد"))
    }

    @Test
    fun `arabic indic digits become ascii digits`() = assertEquals("0123456789", normalizeSearch("٠١٢٣٤٥٦٧٨٩"))

    @Test
    fun `spaces are trimmed and collapsed`() = assertEquals("a b", normalizeSearch("  a   b  "))

    @Test
    fun `blank input gives an empty text`() {
        assertEquals("", normalizeSearch(""))
        assertEquals("", normalizeSearch("   "))
    }

    @Test
    fun `two spellings of the same word normalize equally`() {
        assertEquals(normalizeSearch("صيدلية الإيمان"), normalizeSearch("صيدليه الايمان"))
    }

    @Test
    fun `searchTextOf joins normalized parts and skips null or blank`() {
        assertEquals("احمد cairo 12", searchTextOf("أحمد", null, "  ", "Cairo", "١٢"))
    }
}
