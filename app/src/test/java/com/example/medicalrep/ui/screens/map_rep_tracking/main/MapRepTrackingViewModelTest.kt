package com.example.medicalrep.ui.screens.map_rep_tracking.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.repo.FakeTrackPointRepo
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.domain.repo.RepDayRequest
import com.example.medicalrep.domain.repo.RouteRepo
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Given a rep and a day (the route args), and their completed visits and GPS points in Room:
 * 1. The visits are those the rep completed that day, the first one is selected; the track is the rep's points that day.
 * 2. The road route is requested through every track point in order; with fewer than two points it is not requested.
 * 3. The start and end icons sit at the ends of the road line (else at the first and last GPS point); neither with fewer than two points.
 * 3b. A visit pin is the place of the account; a visit whose account has no location has no pin but stays listed.
 * 4. A route error is kept and Retry reloads it.
 * 5. A known marker selects its visit; an unknown id keeps the selection. No visits -> nothing selected.
 * 6. Open details -> Navigate(VisitDetails(id)). Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapRepTrackingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val day = startOfDay(1_792_000_000_000L)
    private val nextDay = startOfNextDay(day)
    private val minute = 60_000L
    private val routeRepo = FakeRouteRepo()
    private val visitRepo = FakeVisitRepo(
        listOf(
            visit("v2", day + 600 * minute, 30.07, 31.30),
            visit("v1", day + 540 * minute, 30.05, 31.25),
            visit("other-rep", day + 560 * minute, 30.06, 31.27, repId = "rep-2"),
            visit("next-day", nextDay + minute, 30.09, 31.40),
            visit("no-place", day + 700 * minute, 0.0, 0.0),
        ),
    )
    private val trackPointRepo = FakeTrackPointRepo().apply {
        stored.value = listOf(
            point(1, day + 545 * minute, 30.051, 31.251),
            point(2, day + 530 * minute, 30.040, 31.240),
            point(3, day + 570 * minute, 30.060, 31.270),
            point(4, day + 570 * minute, 30.060, 31.270, repId = "rep-2"),
            point(5, nextDay + minute, 30.1, 31.5),
        )
    }
    private lateinit var viewModel: MapRepTrackingViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = create()
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `shows the rep's completed visits of the day with the first one selected`() {
        assertEquals(listOf("v1", "v2", "no-place"), state.visits.map { it.id })
        assertEquals("v1", state.selectedVisit?.id)
        assertEquals(RepDayRequest("rep-1", day, nextDay), visitRepo.doneOnDayRequests.single())
    }

    @Test
    fun `the track is the rep's points of the day in time order`() {
        assertEquals(listOf(2, 1, 3), state.routePoints.map { it.id })
        assertEquals(RepDayRequest("rep-1", day, nextDay - 1), trackPointRepo.dayRequests.single())
    }

    @Test
    fun `the start and end icons sit at the ends of the road line once it is loaded`() {
        assertEquals(routeRepo.points.first(), state.trackStart)
        assertEquals(routeRepo.points.last(), state.trackEnd)
    }

    @Test
    fun `without a road line the icons sit at the first and last GPS point`() {
        routeRepo.result = Resource.Error("down")
        viewModel = create()
        assertEquals(GeoPoint(30.040, 31.240), state.trackStart)
        assertEquals(GeoPoint(30.060, 31.270), state.trackEnd)
    }

    @Test
    fun `a track of fewer than two points has no start or end icon`() {
        trackPointRepo.stored.value = trackPointRepo.stored.value.take(1)
        viewModel = create()
        assertNull(state.trackStart)
        assertNull(state.trackEnd)

        trackPointRepo.stored.value = emptyList()
        viewModel = create()
        assertNull(state.trackStart)
        assertNull(state.trackEnd)
    }

    @Test
    fun `a visit pin is the place of the account and a visit without a place has no pin`() {
        assertEquals(listOf("v1", "v2"), state.visitMarkers.map { it.visitId })
        assertEquals(30.05, state.visitMarkers.first().latitude, 0.0)
        assertEquals(31.25, state.visitMarkers.first().longitude, 0.0)
        assertTrue(state.visits.any { it.id == "no-place" })
    }

    @Test
    fun `the road route is requested through every track point in order and stored`() {
        val track = state.routePoints.map { GeoPoint(it.latitude, it.longitude) }
        assertEquals(3, track.size)
        assertEquals(track, routeRepo.lastWaypoints)
        assertEquals(routeRepo.route, state.route.data)
    }

    @Test
    fun `fewer than two track points ask for no route and the visits still show`() {
        trackPointRepo.stored.value = trackPointRepo.stored.value.take(1)
        routeRepo.lastWaypoints = null
        viewModel = create()
        assertNull(routeRepo.lastWaypoints)
        assertEquals(1, state.routePoints.size)
        assertEquals(3, state.visits.size)

        trackPointRepo.stored.value = emptyList()
        viewModel = create()
        assertNull(routeRepo.lastWaypoints)
        assertTrue(state.routePoints.isEmpty())
    }

    @Test
    fun `a rep with no visits has nothing selected`() {
        visitRepo.current.value = emptyList()
        viewModel = create()
        assertTrue(state.visits.isEmpty() && state.visitMarkers.isEmpty())
        assertNull(state.selectedVisit)
        assertNull(MapRepTrackingState().selectedVisit)
    }

    @Test
    fun `route error is kept in state and retry reloads it`() {
        routeRepo.result = Resource.Error("down")
        viewModel = create()
        assertTrue(state.route is Resource.Error)

        routeRepo.result = Resource.Success(routeRepo.route)
        viewModel.onEvent(MapRepTrackingEvents.RetryRoute)
        assertEquals(routeRepo.route, state.route.data)
    }

    @Test
    fun `clicking a marker selects its visit and an unknown id is ignored`() {
        viewModel.onEvent(MapRepTrackingEvents.MarkerClicked("v2"))
        assertEquals("v2", state.selectedVisit?.id)

        viewModel.onEvent(MapRepTrackingEvents.MarkerClicked("missing"))
        assertEquals("v2", state.selectedVisit?.id)
    }

    @Test
    fun `open details navigates to the visit and back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(MapRepTrackingEvents.OpenVisitDetails("v2"))
        viewModel.onEvent(MapRepTrackingEvents.Back)
        assertEquals(
            listOf(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "v2")), UiEvent.NavigationUp),
            events,
        )
    }

    private fun create() = MapRepTrackingViewModel(
        FakeSettingsRepo,
        SavedStateHandle(mapOf("repId" to "rep-1", "dayMillis" to day + 60 * minute)),
        routeRepo,
        visitRepo,
        trackPointRepo,
    )

    private fun visit(id: String, at: Long, latitude: Double, longitude: Double, repId: String = "rep-1") = Visit(
        id = id,
        repId = repId,
        status = VisitStatus.Completed,
        dateTimeMillis = at,
        accountLatitude = latitude,
        accountLongitude = longitude,
    )

    private fun point(id: Long, at: Long, latitude: Double, longitude: Double, repId: String = "rep-1") =
        TrackPoint(id = id, repId = repId, latitude = latitude, longitude = longitude, timeMillis = at)

    private fun TestScope.collectEvents(): MutableList<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        return events
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}

private class FakeRouteRepo : RouteRepo {
    val points = listOf(GeoPoint(30.0, 31.0), GeoPoint(30.1, 31.1))
    val route = RouteResult(points, distanceMeters = 1000, durationSeconds = 600)
    var result: Resource<RouteResult> = Resource.Success(route)
    var lastWaypoints: List<GeoPoint>? = null

    override fun getRoute(waypoints: List<GeoPoint>): Flow<Resource<RouteResult>> {
        lastWaypoints = waypoints
        return flowOf(result)
    }
}
