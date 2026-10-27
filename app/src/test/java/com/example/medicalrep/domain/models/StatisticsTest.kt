package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 1. A class's percent is its share of all accounts, rounded to a whole number (18 of 36 = 50, 12 of 36 = 33, 6 of 36 = 17).
 * 2. With no accounts every percent is 0 (no division by zero).
 * 3. One class holding every account is 100.
 * 4. The total is the sum of the classes.
 */
class StatisticsTest {

    private val statistics = Statistics(classes = listOf(ClassShare("A", 18), ClassShare("B", 12), ClassShare("C", 6)))

    @Test
    fun `percent is the share of all accounts rounded to a whole number`() {
        assertEquals(listOf(50, 33, 17), statistics.classes.map(statistics::percentOf))
    }

    @Test
    fun `no accounts gives zero percent`() {
        val empty = Statistics(classes = listOf(ClassShare("A", 0), ClassShare("B", 0)))
        assertEquals(0, empty.totalAccounts)
        assertEquals(listOf(0, 0), empty.classes.map(empty::percentOf))
        assertEquals(0, Statistics().totalAccounts)
    }

    @Test
    fun `one class with every account is a hundred percent`() {
        val one = Statistics(classes = listOf(ClassShare("A", 7), ClassShare("B", 0)))
        assertEquals(listOf(100, 0), one.classes.map(one::percentOf))
    }

    @Test
    fun `the total is the sum of the classes`() {
        assertEquals(36, statistics.totalAccounts)
    }
}
