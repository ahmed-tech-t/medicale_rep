package com.example.medicalrep.ui.screens.planvisit.account_location.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class DirectionsUrlTest {

    @Test
    fun `url asks Google Maps for driving directions to the account`() {
        assertEquals(
            "https://www.google.com/maps/dir/?api=1&destination=30.5657,31.0043&travelmode=driving",
            directionsUrl(latitude = 30.5657, longitude = 31.0043),
        )
    }
}
