package com.example.medicalrep.ui.screens.planvisit.account_directions.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/** Walking time is the distance at the walking speed: 13 km at 5 km/h is 2 h 36 min. */
class TravelTimeTest {

    @Test
    fun `walking estimate follows the walking speed`() {
        assertEquals(9_360, estimateWalkingSeconds(13_000), 60)
        assertEquals(0, estimateWalkingSeconds(0))
    }

    private fun assertEquals(expected: Int, actual: Int, delta: Int) =
        org.junit.Assert.assertTrue("expected $expected +- $delta but was $actual", kotlin.math.abs(expected - actual) <= delta)
}
