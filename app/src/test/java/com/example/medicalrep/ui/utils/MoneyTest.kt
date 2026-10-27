package com.example.medicalrep.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/** Money shows with thousands separators and at most two decimals, never trailing zeros, always with Latin digits. */
class MoneyTest {
    @Test
    fun `whole amounts have no decimals`() {
        assertEquals("100", formatMoney(100.0))
        assertEquals("0", formatMoney(0.0))
    }

    @Test
    fun `decimals are kept without trailing zeros`() {
        assertEquals("1,388.8", formatMoney(1388.8))
        assertEquals("775.5", formatMoney(775.5))
        assertEquals("694.4", formatMoney(694.4))
    }

    @Test
    fun `thousands are separated and decimals stop at two`() {
        assertEquals("1,234,567.89", formatMoney(1_234_567.891))
    }
}
