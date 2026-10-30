package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

/** A stock sums what came in; what is left already loses what the visits gave; used is the rest. */
class StockCountTest {

    @Test
    fun `used is what came in minus what is left`() {
        val stock = StockCount.of(
            listOf(
                Custody("c1", "Pen", received = 150, remaining = 40),
                Custody("c2", "Cap", received = 100, remaining = 45),
            ),
        )
        assertEquals(StockCount(total = 250, used = 165, remaining = 85), stock)
    }

    @Test
    fun `a stock of a total and what went out keeps the rest`() {
        assertEquals(StockCount(total = 1200, used = 860, remaining = 340), StockCount.of(total = 1200, used = 860))
    }

    @Test
    fun `more went out than came in leaves nothing, not a negative number`() {
        assertEquals(StockCount(total = 10, used = 12, remaining = 0), StockCount.of(total = 10, used = 12))
    }

    @Test
    fun `no custody gives zeros`() {
        assertEquals(StockCount(0, 0, 0), StockCount.of(emptyList()))
    }

    @Test
    fun `nothing given out leaves everything`() {
        assertEquals(StockCount(total = 10, used = 0, remaining = 10), StockCount.of(listOf(Custody("c", "Pen", received = 10))))
    }
}
