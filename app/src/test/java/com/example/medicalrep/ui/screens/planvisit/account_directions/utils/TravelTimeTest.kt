package com.example.medicalrep.ui.screens.planvisit.account_directions.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/** Walking time is the distance at the walking speed: 13 km at 3.7 km/h is about 3 h 31 min. */
class TravelTimeTest {

    @Test
    fun `walking estimate follows the walking speed`() {
        assertEquals(12_650, estimateWalkingSeconds(13_000), 60)
        assertEquals(0, estimateWalkingSeconds(0))
    }

    private fun assertEquals(expected: Int, actual: Int, delta: Int) =
        org.junit.Assert.assertTrue("expected $expected +- $delta but was $actual", kotlin.math.abs(expected - actual) <= delta)
}
