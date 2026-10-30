package com.example.medicalrep.ui.screens.visitlog.main

import com.example.medicalrep.domain.models.BasicModel
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.District
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.FakeDoctorRepo
import com.example.medicalrep.domain.repo.FakeDraftVisitRepo
import com.example.medicalrep.domain.repo.FakeGeoRepo
import com.example.medicalrep.domain.repo.FakeVisitRepo
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.example.medicalrep.ui.components.widgets.filter.ClassFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterEvents
import com.example.medicalrep.ui.components.widgets.filter.SyncFilter
import com.example.medicalrep.ui.components.widgets.filter.VisitTypeFilter
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
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
import kotlinx.coroutines.test.advanceTimeBy
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
 * 4. When the filter icon is tapped, Then the sheet opens with the cities and specialties of the repos.
 * 5. Given a class is chosen, Then nothing is asked until Apply; Apply sends it to the repo, the count follows, the sheet closes.
 * 6. Given city, district, specialty and a day, When Apply, Then all reach the repo (the day is [start, next start)).
 * 7. Choosing another city reloads the districts and clears the chosen district; no city means no districts.
 * 8. Done: sync status and visit type reach the repo; Reset then Apply asks for everything again.
 * 9. Dismissing the sheet keeps the applied filter; applying the same filter twice asks the repo once.
 * 10. Switching tab resets the filter and the form and closes the sheet; the filter icon is on only while filtered.
 * 11. Cancel visit opens the reason sheet; a blank reason cannot be confirmed; a reason cancels the visit once, closes the sheet and
 *     the visit leaves the Required list; back closes it without cancelling; switching tab closes it.
 * 12. Search: asked after a 300 ms pause, trimmed, one ask for fast typing, clearing is instant, kept by Apply, cleared by a tab switch.
 * 13. When Start visit, Then it navigates to the plan-visit screen with that visit id; with a pending visit it asks first.
 * 14. Confirming cancels the pending visit and starts the chosen one. Open visit opens the details. Back navigates up.
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
    private val geoRepo = FakeGeoRepo(
        cities = listOf(BasicModel("c1", "Cairo"), BasicModel("c2", "Giza")),
        districts = listOf(District("d1", "Nasr City", "c1"), District("d2", "Dokki", "c2")),
    )
    private val doctorRepo = FakeDoctorRepo().apply {
        setDoctors("a1", listOf(Doctor(id = "doc1", name = "Dr A", specialty = BasicModel("Cardiology", "Cardiology"))))
    }
    private lateinit var viewModel: VisitLogViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitLogViewModel(FakeSettingsRepo, draftRepo, repo, geoRepo, doctorRepo)
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

    private fun filter(event: FilterEvents) = viewModel.onEvent(VisitLogEvents.OnFilterEvent(event))

    private fun apply() = viewModel.onEvent(VisitLogEvents.ApplyFilter)

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
        assertEquals(listOf(VisitListFilter()), repo.requiredRequests)
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `selecting the done tab asks for done visits and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        assertEquals(listOf(VisitListFilter()), repo.doneRequests)
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
    fun `the filter icon opens the sheet with the cities and specialties`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        runCurrent()
        assertEquals(VisitLogOverlay.Filter, state.overlay)
        assertEquals(listOf("Cairo", "Giza"), state.sheet.cites.map { it.name })
        assertEquals(listOf("Cardiology"), state.sheet.specialties.map { it.name })
        assertTrue(state.sheet.regions.isEmpty())
    }

    @Test
    fun `a chosen class reaches the repo only on apply and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.B))
        runCurrent()
        assertEquals(VisitListFilter(), repo.requiredRequests.last())
        assertFalse(state.isFiltered)
        apply()
        runCurrent()
        assertEquals(VisitListFilter(classCategory = "B"), repo.requiredRequests.last())
        assertEquals(1, state.resultCount)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertTrue(state.isFiltered)
    }

    @Test
    fun `city district specialty and a day reach the repo`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnCitySelect(BasicModelUi("c1", "Cairo")))
        runCurrent()
        filter(FilterEvents.OnRegionSelect(BasicModelUi("d1", "Nasr City")))
        filter(FilterEvents.OnSpecialtySelect(BasicModelUi("Cardiology", "Cardiology")))
        val day = startOfDay(System.currentTimeMillis() - 3 * DAY)
        filter(FilterEvents.OnStartDateSelect(day))
        filter(FilterEvents.OnEndDateSelect(day))
        apply()
        runCurrent()
        assertEquals(
            VisitListFilter(
                cityId = "c1",
                districtId = "d1",
                specialty = "Cardiology",
                fromMillis = startOfDay(day),
                toMillis = startOfNextDay(day),
            ),
            repo.requiredRequests.last(),
        )
    }

    @Test
    fun `another city reloads the districts and clears the chosen one`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnCitySelect(BasicModelUi("c1", "Cairo")))
        runCurrent()
        assertEquals(listOf("Nasr City"), state.sheet.regions.map { it.name })
        filter(FilterEvents.OnRegionSelect(BasicModelUi("d1", "Nasr City")))
        filter(FilterEvents.OnCitySelect(BasicModelUi("c2", "Giza")))
        runCurrent()
        assertEquals(listOf("Dokki"), state.sheet.regions.map { it.name })
        assertEquals(null, state.sheet.selectedRegion)
    }

    @Test
    fun `done filters reach the repo and the count follows`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnVisitTypeSelect(VisitTypeFilter.Double))
        apply()
        runCurrent()
        assertEquals(VisitListFilter(visitMode = VisitMode.Double), repo.doneRequests.last())
        assertEquals(1, state.resultCount)
        filter(FilterEvents.OnVisitTypeSelect(VisitTypeFilter.All))
        filter(FilterEvents.OnSyncFilterSelect(SyncFilter.Synced))
        apply()
        runCurrent()
        assertEquals(VisitListFilter(syncStatus = SyncStatus.Synced), repo.doneRequests.last())
        assertEquals(1, state.resultCount)
        filter(FilterEvents.OnSyncFilterSelect(SyncFilter.Waiting))
        filter(FilterEvents.OnVisitTypeSelect(VisitTypeFilter.Single))
        apply()
        runCurrent()
        assertEquals(VisitListFilter(syncStatus = SyncStatus.NotSynced, visitMode = VisitMode.Single), repo.doneRequests.last())
        filter(FilterEvents.OnSyncFilterSelect(SyncFilter.Failed))
        filter(FilterEvents.OnVisitTypeSelect(VisitTypeFilter.All))
        apply()
        runCurrent()
        assertEquals(VisitListFilter(syncStatus = SyncStatus.Failed), repo.doneRequests.last())
    }

    @Test
    fun `reset then apply asks for everything again`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.A))
        apply()
        runCurrent()
        assertTrue(state.isFiltered)
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnReset)
        apply()
        runCurrent()
        assertEquals(VisitListFilter(), repo.requiredRequests.last())
        assertFalse(state.isFiltered)
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `dismissing keeps the applied filter and applying the same one twice asks once`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.A))
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        runCurrent()
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(VisitListFilter(), state.filter)
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        apply()
        apply()
        runCurrent()
        assertEquals(listOf(VisitListFilter(), VisitListFilter(classCategory = "A")), repo.requiredRequests)
    }

    @Test
    fun `no visit matching the filter counts zero`() = runTest(dispatcher) {
        repo.current.value = emptyList()
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.C))
        apply()
        runCurrent()
        assertEquals(0, state.resultCount)
        assertTrue(state.isFiltered)
    }

    @Test
    fun `switching tab resets the filter and the form and closes the sheet`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.A))
        apply()
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        filter(FilterEvents.OnClassSelect(ClassFilter.B))
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        assertEquals(VisitListFilter(), state.filter)
        assertEquals(ClassFilter.All, state.sheet.selectedClass)
        assertFalse(state.isFiltered)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(2, state.resultCount)
        assertEquals(listOf("Cairo", "Giza"), state.sheet.cites.map { it.name })
    }

    @Test
    fun `filter icon opens the sheet and dismiss closes it`() {
        viewModel.onEvent(VisitLogEvents.OpenFilter)
        assertEquals(VisitLogOverlay.Filter, state.overlay)
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, state.overlay)
    }

    private fun TestScope.openCancelSheet(id: String = "r1") {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.CancelVisit(id))
        runCurrent()
    }

    @Test
    fun `cancel visit opens an empty reason sheet and does not cancel`() = runTest(dispatcher) {
        openCancelSheet()
        assertEquals(VisitLogOverlay.CancelVisit("r1"), state.overlay)
        assertEquals("", state.cancelReason.text.toString())
        assertTrue(repo.cancelCalls.isEmpty())
    }

    @Test
    fun `a blank reason cannot be confirmed`() = runTest(dispatcher) {
        openCancelSheet()
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        state.cancelReason.setTextAndPlaceCursorAtEnd("   ")
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        runCurrent()
        assertTrue(repo.cancelCalls.isEmpty())
        assertEquals(VisitLogOverlay.CancelVisit("r1"), state.overlay)
    }

    @Test
    fun `a reason cancels the visit once, closes the sheet and the visit leaves the list`() = runTest(dispatcher) {
        openCancelSheet()
        assertEquals(3, state.resultCount)
        state.cancelReason.setTextAndPlaceCursorAtEnd("  Doctor travelling  ")
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        runCurrent()
        assertEquals(listOf("r1" to "Doctor travelling"), repo.cancelCalls)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(2, state.resultCount)
    }

    @Test
    fun `a visit that is no longer to do closes the sheet and changes nothing`() = runTest(dispatcher) {
        openCancelSheet("d1")
        state.cancelReason.setTextAndPlaceCursorAtEnd("late")
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        runCurrent()
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertEquals(3, state.resultCount)
    }

    @Test
    fun `back closes the cancel sheet without cancelling and the next one starts empty`() = runTest(dispatcher) {
        openCancelSheet()
        state.cancelReason.setTextAndPlaceCursorAtEnd("typed")
        viewModel.onEvent(VisitLogEvents.DismissOverlay)
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertTrue(repo.cancelCalls.isEmpty())
        viewModel.onEvent(VisitLogEvents.CancelVisit("r2"))
        assertEquals(VisitLogOverlay.CancelVisit("r2"), state.overlay)
        assertEquals("", state.cancelReason.text.toString())
    }

    @Test
    fun `switching tab closes the cancel sheet without cancelling`() = runTest(dispatcher) {
        openCancelSheet()
        state.cancelReason.setTextAndPlaceCursorAtEnd("typed")
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        viewModel.onEvent(VisitLogEvents.ConfirmCancel)
        runCurrent()
        assertEquals(VisitLogOverlay.None, state.overlay)
        assertTrue(repo.cancelCalls.isEmpty())
    }

    private fun search(text: String) = viewModel.onEvent(VisitLogEvents.OnSearchChange(text))

    @Test
    fun `search asks the repo only after typing pauses and trims the text`() = runTest(dispatcher) {
        collectVisits()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        runCurrent()
        search("  Mona ")
        runCurrent()
        assertEquals("", repo.doneRequests.last().query)
        advanceTimeBy(300)
        runCurrent()
        assertEquals("Mona", repo.doneRequests.last().query)
        assertEquals("  Mona ", state.query)
    }

    @Test
    fun `typing fast asks the repo once for the last text`() = runTest(dispatcher) {
        collectVisits()
        val before = repo.requiredRequests.size
        search("M")
        advanceTimeBy(100)
        search("Mo")
        advanceTimeBy(100)
        search("Mon")
        advanceTimeBy(300)
        runCurrent()
        assertEquals(listOf("Mon"), repo.requiredRequests.drop(before).map { it.query })
    }

    @Test
    fun `clearing the search restores the list at once and blank text asks for everything`() = runTest(dispatcher) {
        collectVisits()
        search("Mona")
        advanceTimeBy(300)
        runCurrent()
        search("")
        runCurrent()
        assertEquals("", repo.requiredRequests.last().query)
        search("   ")
        advanceTimeBy(300)
        runCurrent()
        assertEquals("", repo.requiredRequests.last().query)
    }

    @Test
    fun `applying the filter keeps the search and the search is not a filter`() = runTest(dispatcher) {
        collectVisits()
        search("Mona")
        advanceTimeBy(300)
        runCurrent()
        assertFalse(state.isFiltered)
        filter(FilterEvents.OnClassSelect(ClassFilter.A))
        apply()
        advanceTimeBy(300)
        runCurrent()
        assertEquals(VisitListFilter(classCategory = "A", query = "Mona"), repo.requiredRequests.last())
        assertTrue(state.isFiltered)
    }

    @Test
    fun `switching tab clears the search and asks the new tab for everything`() = runTest(dispatcher) {
        collectVisits()
        search("Mona")
        advanceTimeBy(300)
        runCurrent()
        viewModel.onEvent(VisitLogEvents.SelectTab(VisitLogTab.Done))
        advanceTimeBy(300)
        runCurrent()
        assertEquals("", state.query)
        assertEquals("", repo.doneRequests.last().query)
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
        const val DAY = 24L * 60 * 60 * 1000

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
