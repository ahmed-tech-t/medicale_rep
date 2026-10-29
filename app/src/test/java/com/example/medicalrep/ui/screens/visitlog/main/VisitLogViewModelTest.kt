package com.example.medicalrep.ui.screens.visitlog.main

import com.example.medicalrep.domain.models.BasicModel
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.FakeDraftVisitRepo
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.navigation.model.MainGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then Required is selected and the repo is asked for every required visit; the count is theirs.
 * 2. When the Done tab is selected, Then the repo is asked for done visits and the count follows; Required restores it.
 * 3. Given no visits, Then the count is 0 on both tabs.
 * 4. Required filters (shift, class) and Done filters (shift, double, sync status) reach the repo; the count follows.
 * 5. Switching tab resets the filter to All and closes the popup; the filter icon is on only while filtered.
 * 6. When Start visit, Then it navigates to the plan-visit screen with that visit id; with a pending visit it asks first.
 * 7. Confirming cancels the pending visit and starts the chosen one. Open visit opens the details. Back navigates up.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitLogViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val draftRepo = FakeDraftVisitRepo()
    private val repo = FakeVisitRepo(
        listOf(
            visit("r1", Shift.Am, "A"),
            visit("r2", Shift.Pm, "B"),
            visit("r3", Shift.Am, "C"),
            visit("d1", Shift.Am, "A", done = true, double = true, syncStatus = SyncStatus.NotSynced),
            visit("d2", Shift.Pm, "B", done = true, syncStatus = SyncStatus.Synced),
        ),
    )
    private lateinit var viewModel: VisitLogViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitLogViewModel(FakeSettingsRepo, draftRepo, repo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Collects the list of the selected tab, and of every tab selected later, like the screen does. */
    private fun TestScope.collectVisits() {
        backgroundScope.launch(dispatcher) {
            viewModel.state.map { it.selectedTab }.distinctUntilChanged().collect { tab ->
                launch { viewModel.visitsOf(tab).collect { } }
            }
        }
        runCurrent()
    }

    private fun TestScope.collectEvents(): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        runCurrent()
        return events
    }

    @Test
    fun `starts on the required tab asking for every required visit`() = runTest(dispatcher) {
        collectVisits()
        assertEquals(VisitLogTab.Required, state.selectedTab)
        assertEquals(listOf<Pair<Shift?, String?>>(null to null), repo.requiredRequests)
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `selecting the done tab asks for done visits and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        assertEquals(listOf(Triple<Shift?, Boolean, SyncStatus?>(null, false, null)), repo.doneRequests)
        assertEquals(2, state.resultCount)
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Required))
        runCurrent()
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `every tab has its own list so a tab never shows the items of the tab before it`() {
        val lists = VisitLogTab.entries.map { viewModel.visitsOf(it) }
        assertEquals(VisitLogTab.entries.size, lists.toSet().size)
        assertTrue(VisitLogTab.entries.all { viewModel.visitsOf(it) === viewModel.visitsOf(it) })
    }

    @Test
    fun `a tab that is not shown does not ask the repo`() = runTest(dispatcher) {
        collectVisits()
        assertTrue(repo.doneRequests.isEmpty())
    }

    @Test
    fun `no visits counts zero on both tabs`() = runTest(dispatcher) {
        repo.current.value = emptyList()
        collectVisits()
        assertEquals(0, state.resultCount)
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        assertEquals(0, state.resultCount)
    }

    @Test
    fun `required filters reach the repo and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Morning))
        runCurrent()
        assertEquals(Shift.Am to null, repo.requiredRequests.last())
        assertEquals(2, state.resultCount)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertTrue(state.isFiltered)
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.ClassB))
        runCurrent()
        assertEquals(null to "B", repo.requiredRequests.last())
        assertEquals(1, state.resultCount)
    }

    @Test
    fun `done filters reach the repo and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.DoubleVisits))
        runCurrent()
        assertEquals(Triple<Shift?, Boolean, SyncStatus?>(null, true, null), repo.doneRequests.last())
        assertEquals(1, state.resultCount)
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Synced))
        runCurrent()
        assertEquals(Triple<Shift?, Boolean, SyncStatus?>(null, false, SyncStatus.Synced), repo.doneRequests.last())
        assertEquals(1, state.resultCount)
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Evening))
        runCurrent()
        assertEquals(Triple<Shift?, Boolean, SyncStatus?>(Shift.Pm, false, null), repo.doneRequests.last())
    }

    @Test
    fun `switching tab resets the filter to All and closes the popup`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectFilter(VisitFilter.Morning))
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        assertEquals(VisitFilter.All, state.filter)
        assertFalse(state.isFiltered)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(2, state.resultCount)
    }

    @Test
    fun `filter icon opens the popup and dismiss closes it`() {
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        assertEquals(VisitLogOverlay.Filter, state.overlay)
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, state.overlay)
    }

    @Test
    fun `start visit opens the plan visit screen with the id`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("r1"))
        runCurrent()
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "r1"))), events)
    }

    @Test
    fun `starting a visit with a pending one asks first`() = runTest(dispatcher) {
        draftRepo.current.value = DraftVisit(0, Visit())
        runCurrent()
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("r1"))
        runCurrent()
        assertEquals(VisitLogOverlay.PendingVisit("r1"), state.overlay)
        assertEquals(0, events.size)
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(0, draftRepo.discardCount)
    }

    @Test
    fun `confirming cancels the pending visit then starts the chosen one`() = runTest(dispatcher) {
        draftRepo.current.value = DraftVisit(0, Visit())
        runCurrent()
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.StartVisit("r1"))
        viewModel.onEvent(VisitLogEvents.ConfirmPending)
        runCurrent()
        assertEquals(1, draftRepo.discardCount)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "r1"))), events)
    }

    @Test
    fun `open visit navigates to the visit details`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.OpenVisit("d1"))
        runCurrent()
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "d1"))), events)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitLogEvents.Back)
        runCurrent()
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    private companion object {
        fun visit(
            id: String,
            shift: Shift,
            classCategory: String,
            done: Boolean = false,
            double: Boolean = false,
            syncStatus: SyncStatus = SyncStatus.Synced,
        ) = Visit(
            id = id,
            type = VisitType.Planned,
            shift = shift,
            classCategory = classCategory?.let { BasicModel(it, it) },
            visitMode = if (double) VisitMode.Double else VisitMode.Single,
            syncStatus = syncStatus,
            status = if (done) VisitStatus.Completed else VisitStatus.Scheduled,
            completedAtMillis = if (done) 1_000L else null,
        )
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
