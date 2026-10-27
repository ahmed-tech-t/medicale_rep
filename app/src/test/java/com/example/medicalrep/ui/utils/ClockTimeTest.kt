package com.example.medicalrep.ui.utils

import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test

/** The day a moment falls on starts at local midnight; a visit's duration shows as minutes:seconds. */
class ClockTimeTest {

    @Test
    fun `start of day is local midnight of the same day`() {
        val noon = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 27, 12, 34, 56) }.timeInMillis
        val midnight = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 27, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        assertEquals(midnight, startOfDay(noon))
    }

    @Test
    fun `start of day of midnight and of the last millisecond is the same day`() {
        val midnight = startOfDay(System.currentTimeMillis())
        assertEquals(midnight, startOfDay(midnight))
        assertEquals(midnight, startOfDay(midnight + 23 * 3_600_000L))
    }

    @Test
    fun `duration shows minutes and seconds`() {
        assertEquals("04:35 د", formatDurationLabel(275_000))
        assertEquals("00:00 د", formatDurationLabel(0))
        assertEquals("75:05 د", formatDurationLabel(4_505_000))
    }

    @Test
    fun `a negative duration shows zero`() {
        assertEquals("00:00 د", formatDurationLabel(-5_000))
    }
}
