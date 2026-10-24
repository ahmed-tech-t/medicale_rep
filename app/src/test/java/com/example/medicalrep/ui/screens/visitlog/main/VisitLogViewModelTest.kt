package com.example.medicalrep.ui.screens.visitlog.main

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
 * 1. Given the screen opens, Then Required is selected and the count is the number of required visits.
 * 2. When the Done tab is selected, Then the done visits and their count show; selecting Required restores them.
 * 3. Given no visits, Then the count is 0 on both tabs.
 * 4. When Start visit, Then it navigates to the plan-visit screen with that visit id.
 * 5. When Filter, Then one "coming soon" snackbar. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitLogViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: VisitLogViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitLogViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts on the required tab with its count`() {
        assertEquals(VisitLogTab.Required, state.selectedTab)
        assertEquals(state.requiredVisits.size, state.resultCount)
        assertTrue(state.resultCount > 0)
    }

    @Test
    fun `selecting a tab switches the visible visits and the count`() {
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        assertEquals(state.doneVisits, state.visibleVisits)
        assertEquals(state.doneVisits.size, state.resultCount)

        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Required))
        assertEquals(state.requiredVisits, state.visibleVisits)
    }

    @Test
    fun `empty state counts zero on both tabs`() {
        val empty = VisitLogState()
        assertEquals(0, empty.resultCount)
        assertEquals(0, empty.copy(selectedTab = VisitLogTab.Done).resultCount)
    }

    @Test
    fun `start visit opens the plan visit screen with the id`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("required-1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "required-1"))), events)
    }

    @Test
    fun `filter shows one snackbar and back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.FilterClick)
        viewModel.onEvent(VisitLogEvents.Back)
        assertEquals(2, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[1])
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
