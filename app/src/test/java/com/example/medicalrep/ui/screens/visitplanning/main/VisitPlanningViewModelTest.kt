package com.example.medicalrep.ui.screens.visitplanning.main

import com.example.medicalrep.domain.models.BasicModel
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.components.widgets.filter.ClassFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterEvents
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.FakeVisitRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
import com.example.medicalrep.domain.models.District
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.repo.DoctorRepo
import com.example.medicalrep.domain.repo.GeoRepo
import com.example.medicalrep.domain.repo.SystemSettingRepo
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.PLAN_DAYS_AHEAD
import com.example.medicalrep.ui.screens.visitplanning.utils.SETTING_PLAN_RANGE_END
import com.example.medicalrep.ui.screens.visitplanning.utils.SETTING_PLAN_RANGE_START
import com.example.medicalrep.ui.utils.startOfDay
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then Visits is selected, the repo is asked for done visits with no filter, the count is theirs,
 *    14 days are offered starting today and nothing is picked.
 * 2. When the Pending tab is selected, Then the repo is asked for every request and the count follows; choosing an answer asks
 *    the repo for that answer and the count follows; choosing All asks for every one again.
 * 3. Search: typing waits for a pause before the repo is asked; clearing the box asks at once; the text is trimmed.
 * 4. Filter sheet: its form is used only after Apply; Apply closes the sheet and the icon turns on; reset and Apply clears it.
 * 5. Picking a day selects it for that visit; picking it again clears it; days of several visits are kept apart and survive a tab switch.
 * 6. Schedule: one pending request per visit and picked day (same account, that day, not uploaded); the picks clear and one snackbar
 *    shows. With nothing picked nothing is stored.
 * 7. Pick another time on a rejected request opens the schedule sheet for it; the sheet's Request stores one pending request for its
 *    account on the chosen date (not the picks); dismissing forgets it.
 * 8. When Back, Then NavigationUp.
 * 9. Settings start+end → days starts at D, size = (end-start)/day.
 * 10. Settings missing → days size = PLAN_DAYS_AHEAD (fallback).
 * 11. bookedDaysFlow emits → state.bookedDays contains those entries.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitPlanningViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeVisitRepo(
        listOf(
            done("d1", "a1", Shift.Am, "A"),
            done("d2", "a2", Shift.Pm, "B"),
            done("d3", "a3", Shift.Am, "C"),
            Visit(id = "p1", type = VisitType.Planning, status = VisitStatus.PendingApproval, shift = Shift.Am, classCategory = BasicModel("A", "A")),
            Visit(id = "p2", type = VisitType.Planning, status = VisitStatus.Rejected, shift = Shift.Pm, classCategory = BasicModel("B", "B")),
            Visit(id = "p3", type = VisitType.Planning, status = VisitStatus.PendingApproval, shift = Shift.Pm, classCategory = BasicModel("B", "B")),
        ),
    )
    private val existing = setOf("d1", "d2", "d3", "p1", "p2", "p3")
    private val fakeSettingRepo = FakeSystemSettingRepo()
    private lateinit var viewModel: VisitPlanningViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitPlanningViewModel(FakeSettingsRepo, repo, fakeSettingRepo, FakeGeoRepo, FakeDoctorRepo)
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
    fun `starts on visits asking for done visits with their count and the days to plan`() = runTest(dispatcher) {
        collectVisits()
        assertEquals(VisitPlanningTab.Visits, state.selectedTab)
        assertEquals(listOf(VisitListFilter()), repo.doneRequests)
        assertEquals(3, state.resultCount)
        assertEquals(PLAN_DAYS_AHEAD, state.days.size)
        assertEquals(startOfDay(System.currentTimeMillis()), state.days.first().millis)
        assertFalse(state.hasSelection)
        assertFalse(state.isFiltered)
    }

    @Test
    fun `pending tab asks for every request and an answer narrows it`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        runCurrent()
        assertEquals(listOf<ApprovalStatus?>(null), repo.requestsAsked)
        assertEquals(3, state.resultCount)
        viewModel.onEvent(VisitPlanningEvents.SelectApproval(ApprovalStatus.Rejected))
        runCurrent()
        assertEquals(ApprovalStatus.Rejected, repo.requestsAsked.last())
        assertEquals(1, state.resultCount)
        viewModel.onEvent(VisitPlanningEvents.SelectApproval(ApprovalStatus.Pending))
        runCurrent()
        assertEquals(2, state.resultCount)
        viewModel.onEvent(VisitPlanningEvents.SelectApproval(null))
        runCurrent()
        assertEquals(null, repo.requestsAsked.last())
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `every tab has its own list so a tab never shows the items of the tab before it`() {
        val lists = VisitPlanningTab.entries.map { viewModel.visitsOf(it) }
        assertEquals(VisitPlanningTab.entries.size, lists.toSet().size)
        assertTrue(VisitPlanningTab.entries.all { viewModel.visitsOf(it) === viewModel.visitsOf(it) })
    }

    @Test
    fun `search waits for a pause, is trimmed, and clearing it asks at once`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitPlanningEvents.OnSearchChange("  mona "))
        runCurrent()
        assertEquals("", repo.doneRequests.last().query)
        advanceTimeBy(300)
        runCurrent()
        assertEquals("mona", repo.doneRequests.last().query)
        assertFalse(state.isFiltered)
        viewModel.onEvent(VisitPlanningEvents.OnSearchChange(""))
        runCurrent()
        assertEquals("", repo.doneRequests.last().query)
    }

    @Test
    fun `the filter sheet is used after apply and resetting it clears the filter`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitPlanningEvents.OpenFilter)
        assertEquals(VisitPlanningOverlay.Filter, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.OnFilterEvent(FilterEvents.OnClassSelect(ClassFilter.B)))
        runCurrent()
        assertEquals(null, repo.doneRequests.last().classCategory)
        viewModel.onEvent(VisitPlanningEvents.ApplyFilter)
        runCurrent()
        assertEquals("B", repo.doneRequests.last().classCategory)
        assertEquals(1, state.resultCount)
        assertTrue(state.isFiltered)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.OnFilterEvent(FilterEvents.OnReset))
        viewModel.onEvent(VisitPlanningEvents.ApplyFilter)
        runCurrent()
        assertEquals(null, repo.doneRequests.last().classCategory)
        assertEquals(3, state.resultCount)
        assertFalse(state.isFiltered)
    }

    @Test
    fun `picking a day selects it for that visit only and keeps it across tabs`() {
        val (today, tomorrow) = state.days
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", today.millis))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", tomorrow.millis))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d2", today.millis))
        assertEquals(mapOf("d1" to setOf(today.millis, tomorrow.millis), "d2" to setOf(today.millis)), state.selectedDays)
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        assertTrue(state.hasSelection)
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", today.millis))
        assertEquals(setOf(tomorrow.millis), state.selectedDays["d1"])
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", tomorrow.millis))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d2", today.millis))
        assertFalse(state.hasSelection)
    }

    @Test
    fun `scheduling stores one pending request per visit and picked day and clears the picks`() = runTest(dispatcher) {
        val events = collectEvents()
        val (today, tomorrow) = state.days
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", today.millis))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", tomorrow.millis))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d3", tomorrow.millis))
        viewModel.onEvent(VisitPlanningEvents.ScheduleSelectedDays)
        runCurrent()
        val requests = repo.current.value.filter { it.id !in existing }
        assertEquals(3, requests.size)
        assertEquals(3, requests.map { it.id }.toSet().size)
        assertEquals(listOf("a1" to today.millis, "a1" to tomorrow.millis, "a3" to tomorrow.millis), requests.map { it.accountId to it.dateTimeMillis }.sortedBy { it.first + it.second })
        assertTrue(requests.all { it.type == VisitType.Planning && it.status == VisitStatus.PendingApproval && it.syncStatus == SyncStatus.NotSynced })
        assertFalse(state.hasSelection)
        assertEquals(1, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
    }

    @Test
    fun `scheduling with nothing picked stores nothing`() = runTest(dispatcher) {
        val events = collectEvents()
        val before = repo.current.value.size
        viewModel.onEvent(VisitPlanningEvents.ScheduleSelectedDays)
        runCurrent()
        assertEquals(before, repo.current.value.size)
        assertEquals(0, events.size)
    }

    @Test
    fun `picking another time for a rejected request requests that account again on the chosen date`() = runTest(dispatcher) {
        collectEvents()
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("d1", state.days.first().millis))
        viewModel.onEvent(VisitPlanningEvents.PickAnotherTime(VisitUi(id = "p2")))
        assertEquals(VisitPlanningOverlay.ScheduleVisit, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.DismissOverlay)
        assertEquals(null, state.rescheduleId)
        viewModel.onEvent(VisitPlanningEvents.PickAnotherTime(VisitUi(id = "p2")))
        viewModel.onEvent(VisitPlanningEvents.ToggleDay("p2", 7_000L))
        viewModel.onEvent(VisitPlanningEvents.RequestVisit)
        runCurrent()
        val fresh = repo.current.value.filter { it.id !in existing }
        assertEquals(1, fresh.size)
        assertEquals(VisitStatus.PendingApproval, fresh.single().status)
        assertEquals(7_000L, fresh.single().dateTimeMillis)
        assertEquals(null, state.rescheduleId)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        assertTrue(state.hasSelection)
    }

    @Test
    fun `days come from server range when settings are present`() = runTest(dispatcher) {
        val startD = 1_800_000_000_000L
        val endD = startD + 30 * 86_400_000L
        val vm = VisitPlanningViewModel(
            FakeSettingsRepo, repo,
            FakeSystemSettingRepo(mapOf(SETTING_PLAN_RANGE_START to "$startD", SETTING_PLAN_RANGE_END to "$endD")),
            FakeGeoRepo, FakeDoctorRepo,
        )
        runCurrent()
        assertEquals(30, vm.state.value.days.size)
    }

    @Test
    fun `days fall back to PLAN_DAYS_AHEAD when settings are missing`() = runTest(dispatcher) {
        runCurrent()
        assertEquals(PLAN_DAYS_AHEAD, state.days.size)
    }

    @Test
    fun `booked days flow updates state when repo emits`() = runTest(dispatcher) {
        val D = state.days.first().millis
        repo.bookedDaysFlow.value = mapOf("a1" to setOf(D))
        runCurrent()
        assertTrue(state.bookedDays["a1"]?.contains(D) == true)
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
            id = id, accountId = accountId, type = VisitType.Unplanned, shift = shift, classCategory = BasicModel(classCategory, classCategory), status = VisitStatus.Completed, completedAtMillis = 1L,
        )
    }
}

private class FakeSystemSettingRepo(private val data: Map<String, String> = emptyMap()) : SystemSettingRepo {
    override fun get(key: String): Flow<String?> = flowOf(data[key])
    override suspend fun set(key: String, value: String) = Unit
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}

private object FakeGeoRepo : GeoRepo {
    override fun cities(): Flow<List<BasicModel>> = flowOf(emptyList())
    override fun districts(cityId: String?): Flow<List<BasicModel>> = flowOf(emptyList())
    override fun districtsOf(repId: String): Flow<List<BasicModel>> = flowOf(emptyList())
    override suspend fun save(cities: List<BasicModel>, districts: List<District>) = Unit
    override suspend fun assign(repId: String, districtIds: List<String>) = Unit
}

private object FakeDoctorRepo : DoctorRepo {
    override fun doctorsOf(accountId: String): Flow<List<Doctor>> = flowOf(emptyList())
    override fun specialties(): Flow<List<BasicModel>> = flowOf(emptyList())
    override suspend fun save(doctor: Doctor, accountIds: List<String>) = Unit
}
