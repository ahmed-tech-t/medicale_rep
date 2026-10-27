package com.example.medicalrep.ui.utils

import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    private fun at(day: Int, hour: Int = 0, minute: Int = 0, month: Int = Calendar.OCTOBER) =
        Calendar.getInstance().apply { clear(); set(2026, month, day, hour, minute, 0) }.timeInMillis

    @Test
    fun `the week starts on the Saturday at midnight`() {
        val saturday = at(24)
        assertEquals(saturday, startOfWeek(at(27, 15, 30))) // Tuesday
        assertEquals(saturday, startOfWeek(at(24, 12))) // the Saturday itself
        assertEquals(saturday, startOfWeek(at(25))) // Sunday
        assertEquals(saturday, startOfWeek(at(30, 23, 59))) // Friday night
        assertEquals(at(31), startOfWeek(at(31))) // the next Saturday starts a new week
    }

    @Test
    fun `the next week starts seven days later, on the next Saturday`() {
        assertEquals(at(31), startOfNextWeek(at(24)))
        assertEquals(at(31), startOfNextWeek(at(27, 9)))
        assertEquals(at(31), startOfNextWeek(at(30, 23, 59)))
        assertEquals(at(7, month = Calendar.NOVEMBER), startOfNextWeek(at(31)))
    }

    @Test
    fun `a week is seven calendar days even across a clock change`() {
        val start = Calendar.getInstance().apply { timeInMillis = startOfWeek(System.currentTimeMillis()) }
        val next = Calendar.getInstance().apply { timeInMillis = startOfNextWeek(System.currentTimeMillis()) }
        start.add(Calendar.DAY_OF_YEAR, 7)
        assertEquals(start.timeInMillis, next.timeInMillis)
        assertEquals(Calendar.SATURDAY, next.get(Calendar.DAY_OF_WEEK))
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
