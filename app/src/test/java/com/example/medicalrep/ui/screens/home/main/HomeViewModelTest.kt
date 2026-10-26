package com.example.medicalrep.ui.screens.home.main

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.models.StaticsMetricUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD cases (Given / When / Then):
 * 1. Given a new Home, When it opens, Then it shows the user, an active visit, today's visits,
 *    four visit metrics (today, completed, clinics, doctors), custody summary and a closed drawer.
 * 2. Given the drawer is closed, When the menu is tapped, Then the drawer opens.
 * 3. Given the drawer is open, When it is dismissed, Then it closes.
 * 4. Given the drawer is open, When the "Add account" row is tapped, Then the drawer closes
 *    and the add account screen opens.
 * 5. Given Arabic, When the language row is tapped, Then language becomes "en", the drawer closes
 *    and the settings manager publishes "en". Tapping again returns to "ar".
 * 6. Given Home, When sync / continue / cancel / more is tapped,
 *    Then each sends exactly one "coming soon" snackbar.
 * 6b. Given Home, When a today's visit is started, Then it opens the visit screen with that visit id.
 * 7. Given Home, When the add-visit FAB or the drawer "Add visit" row is tapped,
 *    Then it navigates to the unplanned visit screen (drawer closes).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var settingsManager: AppSettingsManager
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        settingsManager = AppSettingsManager(
            FakeHomeSettingsRepo,
            CoreConfig(baseUrl = "", isDebug = false, cryptoKeyAlias = "", defaultLanguage = "ar"),
        )
        viewModel = HomeViewModel(FakeHomeSettingsRepo, settingsManager, LocaleHelper(settingsManager))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val state get() = viewModel.state.value

    @Test
    fun `initial state matches the design sections`() {
        assertEquals("أحمد محمد", state.user.displayName)
        assertNotNull(state.activeVisit)
        assertEquals(2, state.todayVisits.size)
        assertEquals(
            listOf(
                StaticsMetricUi.Today::class,
                StaticsMetricUi.Completed::class,
                StaticsMetricUi.Clinics::class,
                StaticsMetricUi.Doctors::class,
            ),
            state.visitMetrics.map { it::class },
        )
        assertTrue(state.custodySummary.isNotEmpty())
        assertEquals("ar", state.language)
        assertFalse(state.isDrawerOpen)
    }

    @Test
    fun `menu opens the drawer and dismiss closes it`() {
        viewModel.onEvent(HomeEvents.OnMenuClick)
        assertTrue(state.isDrawerOpen)
        viewModel.onEvent(HomeEvents.OnDrawerDismiss)
        assertFalse(state.isDrawerOpen)
    }

    @Test
    fun `add account drawer row closes the drawer and opens the add account screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.AddAccount))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.AddAccount)), events)
    }

    @Test
    fun `terms drawer row opens the terms screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Terms))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.Terms)), events)
    }

    @Test
    fun `language row toggles language and publishes it`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Language))
        assertEquals("en", state.language)
        assertEquals("en", settingsManager.getLanguageSync())
        assertFalse(state.isDrawerOpen)
        assertTrue(events.isEmpty())

        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Language))
        assertEquals("ar", state.language)
        assertEquals("ar", settingsManager.getLanguageSync())
    }

    @Test
    fun `unbuilt actions each show one coming soon snackbar`() = runTest(dispatcher) {
        val events = collectEvents()
        val actions = listOf(
            HomeEvents.OnSyncClick,
            HomeEvents.OnActiveContinue,
            HomeEvents.OnActiveCancel,
            HomeEvents.OnTodayMoreClick,
        )
        actions.forEach(viewModel::onEvent)
        assertEquals(actions.size, events.size)
        assertTrue(events.all { it is UiEvent.ShowSnackBar })
    }

    @Test
    fun `starting a visit opens the visit screen with its id`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnStartVisit("today-1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "today-1"))), events)
    }

    @Test
    fun `add visit FAB and drawer row open the unplanned visit screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnAddVisitClick)
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.AddVisit))
        assertFalse(state.isDrawerOpen)
        assertEquals(
            List(2) { UiEvent.Navigate(MainGraph.PlanVisit()) },
            events,
        )
    }

    @Test
    fun `track reps drawer row opens the rep tracking screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.TrackReps))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.RepTracking)), events)
    }

    @Test
    fun `plan visit drawer row opens the visit planning screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.PlanVisit))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitPlanning)), events)
    }

    @Test
    fun `products drawer row opens the products screen`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Products))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.Products)), events)
    }

    @Test
    fun `my visits drawer row opens the visit log`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.MyVisits))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.VisitLog)), events)
    }

    @Test
    fun `custody drawer row opens samples and gifts`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Custody))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.SampleAndGifts)), events)
    }

    @Test
    fun `information center drawer row opens the data center`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.InfoCenter))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.DataCenter)), events)
    }

    private fun kotlinx.coroutines.test.TestScope.collectEvents(): MutableList<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        return events
    }
}

private object FakeHomeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
