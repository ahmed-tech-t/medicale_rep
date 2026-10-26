package com.example.medicalrep.ui.screens.visitplanning.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then Visits is selected and the count is the visits.
 * 2. When the Visits tab is selected, Then its visits and count show.
 * 3. When a visit is toggled, Then it is selected; toggling again clears it; several can be selected;
 *    the selection survives a tab switch.
 * 4. When Set appointment, Then the schedule sheet opens; Dismiss closes it.
 * 5. When Request visit, Then selection and sheet clear and one snackbar shows.
 * 6. When the date changes, Then state holds it. Filter -> one snackbar. Back -> NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitPlanningViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: VisitPlanningViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitPlanningViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts on visits with its count`() {
        assertEquals(VisitPlanningTab.Visits, state.selectedTab)
        assertEquals(state.visits.size, state.resultCount)
        assertFalse(state.hasSelection)
    }

    @Test
    fun `visits tab shows its visits and count`() {
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Visits))
        assertEquals(state.visits, state.visibleVisits)
        assertEquals(state.visits.size, state.resultCount)
    }

    @Test
    fun `toggle selects and unselects and allows several`() {
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Visits))
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("visit-0"))
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("visit-1"))
        assertEquals(setOf("visit-0", "visit-1"), state.selectedIds)

        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("visit-0"))
        assertEquals(setOf("visit-1"), state.selectedIds)

        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        assertTrue(state.hasSelection)
    }

    @Test
    fun `schedule sheet opens and dismisses`() {
        viewModel.onEvent(VisitPlanningEvents.OpenSchedule)
        assertEquals(VisitPlanningOverlay.ScheduleVisit, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.DismissOverlay)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
    }

    @Test
    fun `request clears selection and sheet and shows one snackbar`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitPlanningEvents.ToggleSelect("visit-0"))
        viewModel.onEvent(VisitPlanningEvents.OpenSchedule)
        viewModel.onEvent(VisitPlanningEvents.RequestVisit)
        assertFalse(state.hasSelection)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        assertEquals(1, events.size)
        assertTrue(events.single() is UiEvent.ShowSnackBar)
    }

    @Test
    fun `date change is stored`() {
        viewModel.onEvent(VisitPlanningEvents.DateChanged(1234L))
        assertEquals(1234L, state.scheduleDateMillis)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitPlanningEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    @Test
    fun `filter icon opens the popup and dismiss closes it`() {
        viewModel.onEvent(VisitPlanningEvents.OpenFilter)
        assertEquals(VisitPlanningOverlay.Filter, state.overlay)
        viewModel.onEvent(VisitPlanningEvents.DismissOverlay)
        assertEquals(VisitPlanningOverlay.None, state.overlay)
    }

    @Test
    fun `selecting a filter narrows the tab list, closes the popup and turns the icon on`() {
        assertEquals(false, state.isFiltered)
        viewModel.onEvent(VisitPlanningEvents.OpenFilter)
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.Morning))
        assertEquals(listOf("visit-0", "visit-2"), state.visibleVisits.map { it.id })
        assertEquals(VisitPlanningOverlay.None, state.overlay)
        assertEquals(true, state.isFiltered)
    }

    @Test
    fun `class filter works on both tabs and All restores`() {
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.ClassB))
        assertEquals(listOf("visit-1"), state.visibleVisits.map { it.id })
        viewModel.onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.Pending))
        assertEquals(listOf("pending-1", "pending-4"), state.visibleVisits.map { it.id })
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.All))
        assertEquals(state.pendingVisits.size, state.resultCount)
        assertEquals(false, state.isFiltered)
    }

    @Test
    fun `evening and class C pick the matching visits`() {
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.Evening))
        assertEquals(listOf("visit-1", "visit-3"), state.visibleVisits.map { it.id })
        viewModel.onEvent(VisitPlanningEvents.SelectFilter(VisitFilter.ClassC))
        assertEquals(listOf("visit-2"), state.visibleVisits.map { it.id })
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
