package com.example.medicalrep.ui.screens.planvisit.account_directions.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.domain.repo.GeocodingRepo
import com.example.medicalrep.domain.repo.RouteRepo
import com.example.medicalrep.domain.repo.FakeLastLocationRepo
import com.example.medicalrep.ui.common.location.LocationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the rep is far from the account, Then the route [rep, account] is requested and stored, with the distance.
 * 2. Given the rep is within 50 m of the account, Then no route is requested and there is no line at all.
 * 3. Given no rep location, Then no distance and no route.
 * 4. Given the route request fails (e.g. no internet), Then a straight line rep -> account is drawn and no car or walking time is shown.
 * 5. Given a rep location, Then the capture time and street are stored; a failed street lookup keeps the rest.
 * 6. When RefreshLocation, Then the location is read again; with no new fix the old one is kept.
 * 7. When ToggleCard, Then the card collapses, and expands again. Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AccountDirectionsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    // Route args are not readable from SavedStateHandle in JVM tests, so the account stays at (0, 0).
    private val account = GeoPoint(latitude = 0.0, longitude = 0.0)
    private val far = GeoPoint(latitude = 0.1, longitude = 0.0) // about 11 km
    private val near = GeoPoint(latitude = 0.0001, longitude = 0.0) // about 11 m
    private val routeRepo = FakeRouteRepo()
    private val geocodingRepo = FakeGeocodingRepo()
    private val lastLocationRepo = FakeLastLocationRepo()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(userLocation: GeoPoint?): AccountDirectionsViewModel {
        lastLocationRepo.location = userLocation
        return AccountDirectionsViewModel(
            FakeSettingsRepo,
            SavedStateHandle(
                mapOf(
                    "accountLatitude" to account.latitude,
                    "accountLongitude" to account.longitude,
                    "accountName" to "Clinic",
                )
            ),
            lastLocationRepo,
            routeRepo,
            geocodingRepo,
        )
    }

    @Test
    fun `far rep gets the route to the account and the distance`() {
        val state = createViewModel(far).state.value
        assertEquals(listOf(GeoPoint(far.latitude, far.longitude), GeoPoint(account.latitude, account.longitude)), routeRepo.lastWaypoints)
        assertEquals(routeRepo.route, state.route)
        assertEquals(routeRepo.route.points, state.lineToAccount)
        assertEquals(routeRepo.route.durationSeconds, state.carSeconds)
        assertEquals(13_000, state.travelDistanceMeters)
        assertNotNull(state.walkingSeconds)
        assertFalse(state.isAtAccount)
        assertTrue(state.distanceToAccountMeters!! > 10_000)
    }

    @Test
    fun `rep within range gets no line`() {
        val state = createViewModel(near).state.value
        assertNull(routeRepo.lastWaypoints)
        assertNull(state.route)
        assertTrue(state.lineToAccount.isEmpty())
        assertTrue(state.isAtAccount)
    }

    @Test
    fun `no location means no distance and no route`() {
        val state = createViewModel(null).state.value
        assertNull(state.distanceToAccountMeters)
        assertTrue(state.lineToAccount.isEmpty())
        assertNull(routeRepo.lastWaypoints)
        assertFalse(state.isAtAccount)
        assertNull(state.capturedAtMillis)
    }

    @Test
    fun `route failure draws a straight line from the rep to the account`() {
        routeRepo.result = Resource.Error("no internet")
        val state = createViewModel(far).state.value
        assertNull(state.route)
        assertNull(state.carSeconds)
        assertNull(state.walkingSeconds)
        assertNull(state.travelDistanceMeters)
        assertEquals(
            listOf(GeoPoint(far.latitude, far.longitude), GeoPoint(account.latitude, account.longitude)),
            state.lineToAccount,
        )
    }

    @Test
    fun `rep location stores the capture time and the street`() {
        val state = createViewModel(far).state.value
        assertNotNull(state.capturedAtMillis)
        assertEquals("Al Geish Street", state.userStreet)
    }

    @Test
    fun `street lookup failure keeps the rest`() {
        geocodingRepo.result = Resource.Error("offline")
        val state = createViewModel(far).state.value
        assertNull(state.userStreet)
        assertNotNull(state.capturedAtMillis)
        assertNotNull(state.route)
    }

    @Test
    fun `refresh reads the location again and keeps the old one without a new fix`() {
        val viewModel = createViewModel(far)
        lastLocationRepo.location = near
        viewModel.onEvent(AccountDirectionsEvents.RefreshLocation)
        assertEquals(near, viewModel.state.value.userLocation)
        assertTrue(viewModel.state.value.isAtAccount)
        assertNull(viewModel.state.value.route)

        lastLocationRepo.location = null
        viewModel.onEvent(AccountDirectionsEvents.RefreshLocation)
        assertEquals(near, viewModel.state.value.userLocation)
        assertFalse(viewModel.state.value.isLocating)
    }

    @Test
    fun `toggle collapses and expands the card and back navigates up`() = runTest(dispatcher) {
        val viewModel = createViewModel(far)
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }

        viewModel.onEvent(AccountDirectionsEvents.ToggleCard)
        assertFalse(viewModel.state.value.isCardExpanded)
        viewModel.onEvent(AccountDirectionsEvents.ToggleCard)
        assertTrue(viewModel.state.value.isCardExpanded)

        viewModel.onEvent(AccountDirectionsEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }
}


private class FakeRouteRepo : RouteRepo {
    val route = RouteResult(
        points = listOf(GeoPoint(30.0, 31.0), GeoPoint(30.1, 31.2)),
        distanceMeters = 13_000,
        durationSeconds = 1_500,
    )
    var result: Resource<RouteResult> = Resource.Success(route)
    var lastWaypoints: List<GeoPoint>? = null

    override fun getRoute(waypoints: List<GeoPoint>): Flow<Resource<RouteResult>> {
        lastWaypoints = waypoints
        return flowOf(result)
    }
}

private class FakeGeocodingRepo : GeocodingRepo {
    var result: Resource<String> = Resource.Success("Al Geish Street")
    override fun getAddress(point: GeoPoint): Flow<Resource<String>> = flowOf(result)
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
