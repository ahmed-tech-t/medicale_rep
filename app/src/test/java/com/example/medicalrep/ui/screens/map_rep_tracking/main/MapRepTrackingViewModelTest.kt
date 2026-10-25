package com.example.medicalrep.ui.screens.map_rep_tracking.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.navigation.model.MainGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the first visit is selected and the route is loaded.
 * 2. When a known marker is clicked, Then that visit is selected; an unknown id keeps the selection.
 * 3. Given no visits, Then nothing is selected (no card).
 * 4. When Open visit details, Then Navigate(VisitDetails(id)). Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapRepTrackingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: MapRepTrackingViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MapRepTrackingViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with the first visit selected and the route loaded`() {
        assertEquals(state.visits.first().id, state.selectedVisit?.id)
        assertEquals(true, state.routePoints.size > 1)
    }

    @Test
    fun `clicking a marker selects its visit and an unknown id is ignored`() {
        val other = state.visits.last().id
        viewModel.onEvent(MapRepTrackingEvents.MarkerClicked(other))
        assertEquals(other, state.selectedVisit?.id)

        viewModel.onEvent(MapRepTrackingEvents.MarkerClicked("missing"))
        assertEquals(other, state.selectedVisit?.id)
    }

    @Test
    fun `no visits means no selected visit`() {
        assertNull(MapRepTrackingState().selectedVisit)
    }

    @Test
    fun `open details navigates to the visit and back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(MapRepTrackingEvents.OpenVisitDetails("rep-2"))
        viewModel.onEvent(MapRepTrackingEvents.Back)
        assertEquals(
            listOf(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "rep-2")), UiEvent.NavigationUp),
            events,
        )
    }

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
