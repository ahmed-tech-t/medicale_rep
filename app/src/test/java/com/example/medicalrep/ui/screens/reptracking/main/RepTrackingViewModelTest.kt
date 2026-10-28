package com.example.medicalrep.ui.screens.reptracking.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.repo.FakeRepRepo
import com.example.medicalrep.domain.repo.FakeStatisticsRepo
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.domain.repo.RepDayRequest
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

/**
 * Given reps and the visits they did:
 * 1. The screen opens on today with the first rep selected; supervisors are not listed.
 * 2. It lists only the completed visits of the selected rep on the selected day; the counts are sums over them.
 * 3. Selecting another rep, or another day, shows that rep's or that day's visits; an unknown rep is ignored.
 * 4. The progress (done, planned, missed) of the selected rep and day is shown.
 * 5. With no reps nothing is selected, nothing is asked for and every count is 0.
 * 6. View route opens the map for the selected rep and day; with no rep it does nothing. Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepTrackingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val day = startOfDay(1_792_000_000_000L)
    private val nextDay = startOfNextDay(day)
    private val hour = 3_600_000L

    private val repRepo = FakeRepRepo(
        listOf(
            Rep("sup-1", "وائل", role = RepRole.Supervisor),
            Rep("rep-1", "أحمد", employeeCode = "1001"),
            Rep("rep-2", "كريم", employeeCode = "1002"),
        ),
    )
    private val visitRepo = FakeVisitRepo(
        listOf(
            done("v1", "rep-1", day + 9 * hour, samples = 2, gifts = 1),
            done("v2", "rep-1", day + 11 * hour, samples = 3),
            done("v3", "rep-2", day + 10 * hour, gifts = 4),
            done("v4", "rep-1", nextDay + 9 * hour),
            done("v5", "rep-1", day + 12 * hour).copy(status = VisitStatus.Scheduled),
        ),
    )
    private val statisticsRepo = FakeStatisticsRepo()
    private lateinit var viewModel: RepTrackingViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = RepTrackingViewModel(FakeSettingsRepo, repRepo, visitRepo, statisticsRepo)
        viewModel.onEvent(RepTrackingEvents.DateChanged(day + hour))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `opens on today with the first rep selected and only reps listed`() {
        val fresh = RepTrackingViewModel(FakeSettingsRepo, repRepo, visitRepo, statisticsRepo)
        assertEquals(listOf("rep-1", "rep-2"), fresh.state.value.reps.map { it.id })
        assertEquals("rep-1", fresh.state.value.selectedRepId)
        assertEquals("أحمد", fresh.state.value.selectedRepName)
        assertTrue(abs(fresh.state.value.dateMillis - System.currentTimeMillis()) < 60_000)
    }

    @Test
    fun `lists the completed visits of the selected rep on the selected day with the counts over them`() {
        assertEquals(listOf("v1", "v2"), state.visits.map { it.id })
        assertEquals(2, state.visitsCount)
        assertEquals(5, state.productsCount)
        assertEquals(1, state.giftsCount)
        assertEquals(RepDayRequest("rep-1", day, nextDay), visitRepo.doneOnDayRequests.last())
    }

    @Test
    fun `selecting another rep shows that rep's visits and an unknown rep is ignored`() {
        viewModel.onEvent(RepTrackingEvents.SelectRep("rep-2"))
        assertEquals(listOf("v3"), state.visits.map { it.id })
        assertEquals(4, state.giftsCount)

        viewModel.onEvent(RepTrackingEvents.SelectRep("missing"))
        assertEquals("rep-2", state.selectedRepId)
        assertEquals(listOf("v3"), state.visits.map { it.id })
    }

    @Test
    fun `selecting another day shows that day's visits, and a day without visits is empty`() {
        viewModel.onEvent(RepTrackingEvents.DateChanged(nextDay + hour))
        assertEquals(listOf("v4"), state.visits.map { it.id })
        assertEquals(RepDayRequest("rep-1", nextDay, startOfNextDay(nextDay)), visitRepo.doneOnDayRequests.last())

        viewModel.onEvent(RepTrackingEvents.DateChanged(day - 10 * 24 * hour))
        assertEquals(emptyList<String>(), state.visits.map { it.id })
        assertEquals(0, state.visitsCount)
        assertEquals(0, state.productsCount)
    }

    @Test
    fun `the progress of the selected rep and day is shown and follows changes`() {
        statisticsRepo.repProgress.value = VisitProgress(completed = 2, planned = 5, missed = 1)
        assertEquals(VisitProgress(completed = 2, planned = 5, missed = 1), state.progress)
        assertEquals(RepDayRequest("rep-1", day, nextDay), statisticsRepo.requestedRepProgress.last())

        statisticsRepo.repProgress.value = VisitProgress(completed = 3, planned = 5, missed = 1)
        assertEquals(3, state.progress.completed)

        viewModel.onEvent(RepTrackingEvents.SelectRep("rep-2"))
        assertEquals("rep-2", statisticsRepo.requestedRepProgress.last().repId)
    }

    @Test
    fun `reps that arrive later are listed and the first one is selected`() {
        val empty = FakeRepRepo()
        val late = RepTrackingViewModel(FakeSettingsRepo, empty, visitRepo, statisticsRepo)
        assertNull(late.state.value.selectedRepId)

        empty.current.value = listOf(Rep("rep-9", "منى"))
        assertEquals("rep-9", late.state.value.selectedRepId)
        assertEquals(listOf("rep-9"), late.state.value.reps.map { it.id })
    }

    @Test
    fun `with no reps nothing is selected and nothing is asked for`() = runTest(dispatcher) {
        val empty = RepTrackingViewModel(FakeSettingsRepo, FakeRepRepo(), FakeVisitRepo(), FakeStatisticsRepo())
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { empty.uiEvent.toList(events) }

        assertNull(empty.state.value.selectedRepId)
        assertEquals("", empty.state.value.selectedRepName)
        assertEquals(0, empty.state.value.visitsCount)
        assertEquals(VisitProgress(), empty.state.value.progress)

        empty.onEvent(RepTrackingEvents.ViewRoute)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `view route opens the map for the selected rep and day and back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(RepTrackingEvents.SelectRep("rep-2"))
        viewModel.onEvent(RepTrackingEvents.ViewRoute)
        assertEquals(
            listOf<UiEvent>(UiEvent.Navigate(MainGraph.MapRepTracking(repId = "rep-2", dayMillis = day + hour))),
            events,
        )

        viewModel.onEvent(RepTrackingEvents.Back)
        assertEquals(UiEvent.NavigationUp, events.last())
    }

    @Test
    fun `clicking a visit opens its details`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(RepTrackingEvents.OpenVisit("v1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitDetails(visitId = "v1"))), events)
    }

    private fun done(id: String, repId: String, at: Long, samples: Int = 0, gifts: Int = 0) = Visit(
        id = id,
        repId = repId,
        status = VisitStatus.Completed,
        dateTimeMillis = at,
        products = if (samples > 0) listOf(Product(id = "s", name = "s", quantity = samples, kind = ProductType.Product)) else emptyList(),
        gifts = if (gifts > 0) listOf(Product(id = "g", name = "g", quantity = gifts, kind = ProductType.Gift)) else emptyList(),
    )

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
