package com.example.medicalrep.ui.screens.reptracking.main

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then a rep is selected and the counts are the sums over the visits.
 * 2. Given no visits, Then every count is 0.
 * 3. When a rep is selected, Then it changes; a blank selection (cleared field) is ignored.
 * 4. When From / To change, Then the range is stored.
 * 5. When View route, Then Navigate(MapRepTracking). Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepTrackingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: RepTrackingViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = RepTrackingViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with a rep and counts summed over the visits`() {
        assertTrue(state.selectedRep.isNotBlank())
        assertEquals(state.visits.size, state.visitsCount)
        assertEquals(state.visits.sumOf { v -> v.products.sumOf { it.quantity } }, state.productsCount)
        assertEquals(state.visits.sumOf { v -> v.gifts.sumOf { it.quantity } }, state.giftsCount)
    }

    @Test
    fun `no visits means zero counts`() {
        val empty = RepTrackingState()
        assertEquals(0, empty.visitsCount)
        assertEquals(0, empty.productsCount)
        assertEquals(0, empty.giftsCount)
    }

    @Test
    fun `selecting a rep changes it and blank is ignored`() {
        val other = state.reps.last()
        viewModel.onEvent(RepTrackingEvents.SelectRep(other))
        assertEquals(other, state.selectedRep)
        viewModel.onEvent(RepTrackingEvents.SelectRep(""))
        assertEquals(other, state.selectedRep)
    }

    @Test
    fun `selected day is stored`() {
        viewModel.onEvent(RepTrackingEvents.DateChanged(10L))
        assertEquals(10L, state.dateMillis)
        viewModel.onEvent(RepTrackingEvents.DateChanged(20L))
        assertEquals(20L, state.dateMillis)
    }

    @Test
    fun `view route opens the map and back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(RepTrackingEvents.ViewRoute)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.MapRepTracking)), events)

        viewModel.onEvent(RepTrackingEvents.Back)
        assertEquals(UiEvent.NavigationUp, events.last())
    }

    @Test
    fun `clicking a visit opens its details`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(RepTrackingEvents.OpenVisit("rep-1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "rep-1"))), events)
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
