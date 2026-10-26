package com.example.medicalrep.ui.screens.map_rep_tracking.utils

import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 1. Given two visits and a fast track between them, Then a Car segment with the minutes between the two GPS times.
 * 2. Given a slow track, Then a Foot segment. Exactly 6 km/h counts as on foot, faster is a car.
 * 3. Then the badge sits on the middle track point of the pair.
 * 4. Given no time passed (or negative), fewer than 2 visits, or a track that is too short, Then no segment.
 * 5. Given a visit that is not exactly on a track point, Then its nearest track point is used.
 * Track points are 0.001 degrees of latitude apart, about 111 m.
 */
class TravelSegmentsTest {

    private fun track(secondsPerPoint: Int, count: Int = 11) = List(count) { index ->
        TrackUserMapMarker(
            id = index,
            latitude = index * 0.001,
            longitude = 0.0,
            visitId = "t$index",
            timeMillis = index * secondsPerPoint * 1000L,
        )
    }

    private fun visitAt(point: TrackUserMapMarker) = point.copy(visitId = "visit")

    @Test
    fun `fast movement is a car with the minutes taken`() {
        val points = track(secondsPerPoint = 60)
        val segment = travelSegments(listOf(visitAt(points[0]), visitAt(points[10])), points).single()
        assertEquals(TravelMode.Car, segment.mode)
        assertEquals(600, segment.seconds)
    }

    @Test
    fun `slow movement is on foot`() {
        val points = track(secondsPerPoint = 120)
        assertEquals(TravelMode.Foot, travelSegments(listOf(visitAt(points[0]), visitAt(points[10])), points).single().mode)
    }

    @Test
    fun `six km per hour counts as on foot and faster is a car`() {
        // 111 m per point: 67 s is 5.96 km/h, 66 s is 6.05 km/h.
        val slow = track(secondsPerPoint = 67)
        val fast = track(secondsPerPoint = 66)
        assertEquals(TravelMode.Foot, travelSegments(listOf(visitAt(slow[0]), visitAt(slow[10])), slow).single().mode)
        assertEquals(TravelMode.Car, travelSegments(listOf(visitAt(fast[0]), visitAt(fast[10])), fast).single().mode)
    }

    @Test
    fun `badge sits on the middle track point`() {
        val points = track(secondsPerPoint = 60)
        val segment = travelSegments(listOf(visitAt(points[2]), visitAt(points[8])), points).single()
        assertEquals(points[5], segment.position)
    }

    @Test
    fun `three visits give two segments`() {
        val points = track(secondsPerPoint = 60)
        val visits = listOf(points[0], points[4], points[10]).map(::visitAt)
        assertEquals(2, travelSegments(visits, points).size)
    }

    @Test
    fun `no time passed, a single visit or a short track give no segment`() {
        val points = track(secondsPerPoint = 0)
        assertTrue(travelSegments(listOf(visitAt(points[0]), visitAt(points[10])), points).isEmpty())

        val moving = track(secondsPerPoint = 60)
        assertTrue(travelSegments(listOf(visitAt(moving[0])), moving).isEmpty())
        assertTrue(travelSegments(listOf(visitAt(moving[0]), visitAt(moving[1])), moving.take(1)).isEmpty())
    }

    @Test
    fun `a visit off the track uses the nearest track point`() {
        val points = track(secondsPerPoint = 60)
        val nearStart = visitAt(points[0]).copy(latitude = 0.00002)
        val nearEnd = visitAt(points[10]).copy(latitude = 0.0099)
        assertEquals(600, travelSegments(listOf(nearStart, nearEnd), points).single().seconds)
    }
}
