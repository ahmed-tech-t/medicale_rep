package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.domain.repo.RouteRepo
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
import org.junit.Before
import org.junit.Test

/**
 * 1. When Back, Then NavigationUp.
 * The distance, line, route fallback and badge rules are in VisitLocationMapStateTest. Decimal route arguments
 * are not readable from SavedStateHandle in JVM tests, so the route request itself is checked on a device.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitLocationMapViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val routeRepo = FakeRouteRepo()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel() = VisitLocationMapViewModel(
        FakeSettingsRepo,
        SavedStateHandle(
            mapOf(
                "visitLatitude" to 30.04,
                "visitLongitude" to 31.23,
                "accountLatitude" to 30.14,
                "accountLongitude" to 31.23,
                "travelSeconds" to 600,
            )
        ),
        routeRepo,
    )

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(VisitLocationMapEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
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
    val route = RouteResult(
        points = listOf(RoutePoint(30.04, 31.23), RoutePoint(30.09, 31.3), RoutePoint(30.14, 31.23)),
        distanceMeters = 13_000,
        durationSeconds = 1_500,
    )
    var result: Resource<RouteResult> = Resource.Success(route)
    var lastWaypoints: List<RoutePoint>? = null

    override fun getRoute(waypoints: List<RoutePoint>): Flow<Resource<RouteResult>> {
        lastWaypoints = waypoints
        return flowOf(result)
    }
}
