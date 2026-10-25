package com.example.medicalrep.ui.screens.visitdetails.utils

import com.example.medicalrep.ui.models.VisitUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 1. Same point -> 0 m; the distance is symmetric.
 * 2. Known pairs give the expected meters (20 m-ish and 750 m-ish samples).
 * 3. <= 50 m is at the account, 51 m is away.
 * 4. A visit with a (0, 0) location has no usable locations.
 */
class VisitDistanceTest {

    @Test
    fun `same point is zero and distance is symmetric`() {
        assertEquals(0, distanceMeters(30.0868, 31.3226, 30.0868, 31.3226))
        assertEquals(
            distanceMeters(30.0868, 31.3226, 30.0866, 31.3225),
            distanceMeters(30.0866, 31.3225, 30.0868, 31.3226),
        )
    }

    @Test
    fun `known pairs`() {
        // 0.0002 deg lat (~22 m) and 0.0001 deg lng (~10 m) at latitude 30.
        assertEquals(24, distanceMeters(30.0868, 31.3226, 30.0866, 31.3225))
        // Account vs. visit from the "away" sample: roughly three quarters of a kilometer.
        assertEquals(750, distanceMeters(30.1030, 31.3267, 30.0975, 31.3312), 30)
    }

    @Test
    fun `fifty meters or less is at the account`() {
        assertTrue(isAtAccountLocation(0))
        assertTrue(isAtAccountLocation(50))
        assertFalse(isAtAccountLocation(51))
    }

    @Test
    fun `unknown locations are detected`() {
        val known = VisitUi(visitLatitude = 30.0, visitLongitude = 31.0, accountLatitude = 30.1, accountLongitude = 31.1)
        assertTrue(known.hasLocations)
        assertFalse(known.copy(visitLatitude = 0.0, visitLongitude = 0.0).hasLocations)
        assertFalse(known.copy(accountLatitude = 0.0, accountLongitude = 0.0).hasLocations)
    }

    private fun assertEquals(expected: Int, actual: Int, delta: Int) =
        assertTrue("expected $expected ± $delta but was $actual", kotlin.math.abs(expected - actual) <= delta)
}
