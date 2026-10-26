package com.example.medicalrep.ui.screens.visitplanning.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.ui.models.VisitFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
 * 1. Given the screen opens, Then Visits is selected, the repo is asked for done visits and the count is theirs.
 * 2. When the Pending tab is selected, Then the repo is asked for pending requests and the count follows.
 * 3. Filters (shift, class) reach the repo for the selected tab, the count follows, the icon is on; the filter stays on tab switch.
 * 4. When a visit is toggled, Then it is selected; toggling again clears it; several can be selected; it survives a tab switch.
 * 5. When Set appointment, Then the schedule sheet opens; Dismiss closes it; a date change is stored.
 * 6. When Request visit, Then one pending plan request is stored per selected visit (same account, chosen date, not uploaded),
 *    the selection and sheet clear and one snackbar shows. With nothing selected nothing is stored.
 * 7. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitPlanningViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeVisitRepo(
        listOf(
            done("d1", "a1", Shift.Am, "A"),
            done("d2", "a2", Shift.Pm, "B"),
            done("d3", "a3", Shift.Am, "C"),
            Visit(id = "p1", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, shift = Shift.Am, classCategory = "A"),
            Visit(id = "p2", type = VisitType.Planning, approvalStatus = ApprovalStatus.Rejected, shift = Shift.Pm, classCategory = "B"),
        ),
    )
    private lateinit var viewModel: VisitPlanningViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitPlanningViewModel(FakeSettingsRepo, repo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.collectVisits() {
        backgroundScope.launch(dispatcher) { viewModel.visits.collect { } }
        runCurrent()
    }

    private fun TestScope.collectEvents(): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        runCurrent()
        return events
    }

    @Test
    fun `starts on visits asking for done visits with their count`() = runTest(dispatcher) {
        collectVisits()
        assertEquals(VisitPlanningTab.Visits, state.selectedTab)
        assertEquals(listOf<Shift?>(null), repo.doneRequests.map { it.first })
        assertEquals(3, state.resultCount)
        assertFalse(state.hasSelection)
    }

    @Test
    fun `pending tab asks for pending requests and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        runCurrent()
        assertEquals(listOf<Pair<Shift?, String?>>(null to null), repo.pendingRequestsAsked)
        assertEquals(2, state.resultCount)
    }

    @Test
    fun `filters reach the repo for the selected tab and stay when the tab changes`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitPlanningEvents.OpenFilter)
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.Morning))
        runCurrent()
        assertEquals(Shift.Am, repo.doneRequests.last().first)
        assertEquals(2, state.resultCount)
        assertTrue(state.isFiltered)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        runCurrent()
        assertEquals(Shift.Am to null, repo.pendingRequestsAsked.last())
        assertEquals(1, state.resultCount)
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.ClassB))
        runCurrent()
        assertEquals(null to "B", repo.pendingRequestsAsked.last())
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.All))
        runCurrent()
        assertFalse(state.isFiltered)
        assertEquals(2, state.resultCount)
    }

    @Test
    fun `toggling selects and clears visits and keeps them across tabs`() {
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d1"))
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d2"))
        assertEquals(setOf("d1", "d2"), state.selectedIds)
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        assertEquals(setOf("d1", "d2"), state.selectedIds)
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d1"))
        assertEquals(setOf("d2"), state.selectedIds)
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d2"))
        assertFalse(state.hasSelection)
    }

    @Test
    fun `schedule sheet opens and dismisses and the date is stored`() {
        viewModel.onEvent(VisitPlanningEvents.OpenSchedule)
        assertEquals(VisitPlanningOverlay.ScheduleVisit, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.DateChanged(1234L))
        assertEquals(1234L, state.scheduleDateMillis)
        viewModel.onEvent(VisitPlanningEvents.DismissOverlay)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
    }

    @Test
    fun `requesting stores one pending request per selected visit and clears the selection`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d1"))
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("d3"))
        viewModel.onEvent(VisitPlanningEvents.DateChanged(5_000L))
        viewModel.onEvent(VisitPlanningEvents.OpenSchedule)
        viewModel.onEvent(VisitPlanningEvents.RequestVisit)
        runCurrent()
        val requests = repo.current.value.filter { it.type == VisitType.Planning && it.id !in setOf("p1", "p2") }
        assertEquals(setOf("a1", "a3"), requests.map { it.accountId }.toSet())
        assertTrue(requests.all { it.approvalStatus == ApprovalStatus.Pending && it.syncStatus == SyncStatus.NotSynced })
        assertTrue(requests.all { it.dateTimeMillis == 5_000L })
        assertEquals(2, requests.map { it.id }.toSet().size)
        assertFalse(state.hasSelection)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        assertEquals(1, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
    }

    @Test
    fun `requesting with nothing selected stores nothing`() = runTest(dispatcher) {
        val events = collectEvents()
        val before = repo.current.value.size
        viewModel.onEvent(VisitPlanningEvents.RequestVisit)
        runCurrent()
        assertEquals(before, repo.current.value.size)
        assertEquals(0, events.size)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitPlanningEvents.Back)
        runCurrent()
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    private companion object {
        fun done(id: String, accountId: String, shift: Shift, classCategory: String) = Visit(
            id = id, accountId = accountId, type = VisitType.Unplanned, shift = shift, classCategory = classCategory, completedAtMillis = 1L,
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
