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
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.repo.FakeDraftVisitRepo
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
    private val draftRepo = FakeDraftVisitRepo()
    private lateinit var viewModel: VisitLogViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitLogViewModel(FakeSettingsRepo, draftRepo)
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
    fun `starting a visit with a pending one asks first`() = runTest(dispatcher) {
        draftRepo.current.value = DraftVisit(0, Visit())
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("required-1"))
        assertEquals(VisitLogOverlay.PendingVisit("required-1"), viewModel.state.value.overlay)
        assertEquals(0, events.size)
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, viewModel.state.value.overlay)
        assertEquals(0, draftRepo.discardCount)
    }

    @Test
    fun `confirming cancels the pending visit then starts the chosen one`() = runTest(dispatcher) {
        draftRepo.current.value = DraftVisit(0, Visit())
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("required-1"))
        viewModel.onEvent(VisitLogEvents.ConfirmPending)
        assertEquals(1, draftRepo.discardCount)
        assertEquals(VisitLogOverlay.None, viewModel.state.value.overlay)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "required-1"))), events)
    }

    @Test
    fun `open visit navigates to the visit details`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.OpenVisit("done-1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "done-1"))), events)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    @Test
    fun `filter icon opens the popup and dismiss closes it`() {
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        assertEquals(VisitLogOverlay.Filter, viewModel.state.value.overlay)
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, viewModel.state.value.overlay)
    }

    @Test
    fun `selecting a filter narrows the tab list, closes the popup and turns the icon on`() {
        assertEquals(false, viewModel.state.value.isFiltered)
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Morning))
        assertEquals(listOf("required-0", "required-2"), viewModel.state.value.visibleVisits.map { it.id })
        assertEquals(2, viewModel.state.value.resultCount)
        assertEquals(VisitLogOverlay.None, viewModel.state.value.overlay)
        assertEquals(true, viewModel.state.value.isFiltered)
    }

    @Test
    fun `required tab filters by shift and class`() {
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Evening))
        assertEquals(listOf("required-1", "required-3"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.ClassA))
        assertEquals(listOf("required-0", "required-3"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.ClassB))
        assertEquals(listOf("required-1"), viewModel.state.value.visibleVisits.map { it.id })
    }

    @Test
    fun `done tab filters by shift, double visit and sync status`() {
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Morning))
        assertEquals(listOf("done-1"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Evening))
        assertEquals(listOf("done-2"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.DoubleVisits))
        assertEquals(listOf("done-1", "done-2"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Synced))
        assertEquals(listOf("done-2"), viewModel.state.value.visibleVisits.map { it.id })
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.NotSynced))
        assertEquals(listOf("done-1"), viewModel.state.value.visibleVisits.map { it.id })
    }

    @Test
    fun `switching tab resets the filter to All and closes the popup`() {
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Morning))
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        assertEquals(VisitFilter.All, viewModel.state.value.filter)
        assertEquals(false, viewModel.state.value.isFiltered)
        assertEquals(VisitLogOverlay.None, viewModel.state.value.overlay)
        assertEquals(viewModel.state.value.doneVisits.size, viewModel.state.value.resultCount)
    }

    @Test
    fun `a filter with no match shows nothing`() {
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.DoubleVisits))
        assertEquals(0, viewModel.state.value.resultCount)
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
