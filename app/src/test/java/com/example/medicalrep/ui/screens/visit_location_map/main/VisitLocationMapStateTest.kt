package com.example.medicalrep.ui.screens.visit_location_map.main

import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.common.travel.TravelMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 1. Given the rep was far from the account, Then the line, the distance and the badge show; fast = car, slow = on foot.
 * 2. Given the rep was within 50 m of the account, Then there is no line and no badge.
 * 3. Given no travel time is known, Then there is no badge (the line still shows).
 * 4. Given the road route loaded, Then its points are the line and its length decides car or on foot.
 * 5. Given no road route (offline or failed), Then the line is the straight one.
 */
class VisitLocationMapStateTest {

    private val rep = MapCameraTarget(latitude = 30.0444, longitude = 31.2357)
    private val account = MapCameraTarget(latitude = 30.1444, longitude = 31.2357) // about 11 km

    private fun state(travelSeconds: Int, visit: MapCameraTarget = rep) =
        VisitLocationMapState(visitLocation = visit, accountLocation = account, travelSeconds = travelSeconds)

    @Test
    fun `far rep gets a line and a car badge when the trip was fast`() {
        val state = state(travelSeconds = 25 * 60)
        assertTrue(state.distanceMeters > 10_000)
        assertFalse(state.isAtAccount)
        assertEquals(2, state.line.size)
        assertTrue(state.showTravel)
        assertEquals(TravelMode.Car, state.mode)
    }

    @Test
    fun `far rep gets an on foot badge when the trip was slow`() {
        assertEquals(TravelMode.Foot, state(travelSeconds = 2 * 60 * 60).mode)
    }

    @Test
    fun `rep within range of the account has no line and no badge`() {
        val state = state(travelSeconds = 600, visit = MapCameraTarget(latitude = 30.14445, longitude = 31.2357))
        assertTrue(state.isAtAccount)
        assertTrue(state.line.isEmpty())
        assertFalse(state.showTravel)
    }

    @Test
    fun `loaded road route is the line and its length decides the mode`() {
        val road = RouteResult(
            points = listOf(RoutePoint(30.0444, 31.2357), RoutePoint(30.07, 31.30), RoutePoint(30.1444, 31.2357)),
            distanceMeters = 30_000,
            durationSeconds = 1_500,
        )
        val onRoad = state(travelSeconds = 5 * 60 * 60).copy(route = road)
        assertEquals(road.points, onRoad.line)
        // 30 km in 5 h is 6 km/h (on foot); in 4 h it is 7.5 km/h (a car).
        assertEquals(TravelMode.Foot, onRoad.mode)
        assertEquals(TravelMode.Car, onRoad.copy(travelSeconds = 4 * 60 * 60).mode)
    }

    @Test
    fun `without a road route the line is straight`() {
        assertEquals(2, state(travelSeconds = 600).copy(route = null).line.size)
    }

    @Test
    fun `unknown travel time shows the line without a badge`() {
        val state = state(travelSeconds = 0)
        assertEquals(2, state.line.size)
        assertFalse(state.showTravel)
    }
}
