package com.example.medicalrep.ui.screens.home.main

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
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
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.ui.common.connectivity.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.repo.FakeStatisticsRepo
import com.appcore.core.data.local.datastore.model.UserDS
import com.example.medicalrep.ui.utils.startOfNextDay
import com.example.medicalrep.ui.utils.startOfNextWeek
import com.example.medicalrep.ui.utils.startOfWeek
import com.example.medicalrep.domain.repo.FakeDraftVisitRepo
import com.example.medicalrep.domain.repo.FakeSession
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.ui.utils.startOfDay
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD cases (Given / When / Then):
 * 1. Given a new Home, When it opens, Then it shows the session user (name, role), the saved street, today's date,
 *    the week's progress, today's visits and a closed drawer.
 * 1b. Today's visits are today's Scheduled visits, soonest first, at most three; the progress is asked for the
 *     Saturday-to-Friday week; the number of visits to upload follows the data.
 * 1c. The quick actions open Visit planning, Samples & gifts and Accounts; Add visit asks first when a draft is pending;
 *     View all opens My visits.
 * 2. Given the drawer is closed, When the menu is tapped, Then the drawer opens.
 * 3. Given the drawer is open, When it is dismissed, Then it closes.
 * 4. Given the drawer is open, When the "Add account" row is tapped, Then the drawer closes
 *    and the add account screen opens.
 * 5. Given Arabic, When the language row is tapped, Then language becomes "en", the drawer closes
 *    and the settings manager publishes "en". Tapping again returns to "ar".
 * 6. Given Home, When View all is tapped, Then My visits opens; the sync button does nothing yet.
 * 6b. Given Home, When a today's visit is started, Then it opens the visit screen with that visit id.
 * 8. Given the network changes, Then the connection state starts Offline, follows Online/Offline, and shouldSync
 *    is true only while Online with visits to upload.
 * 7. Given Home, When the add-visit FAB or the drawer "Add visit" row is tapped,
 *    Then it navigates to the unplanned visit screen (drawer closes).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var settingsManager: AppSettingsManager
    private lateinit var viewModel: HomeViewModel
    private val connectivity = MutableStateFlow<ConnectionState>(ConnectionState.Offline)
    private val draftRepo = FakeDraftVisitRepo()
    private val visitRepo = FakeVisitRepo()
    private val statisticsRepo = FakeStatisticsRepo()
    private val session = FakeSession(UserDS(id = 7, firstName = "Ahmed", lastName = "Mohamed", roleName = "Rep Track", lastAddress = "Union Square"))
    private val draft = DraftVisit(step = 1, visit = Visit(id = "draft", placeName = "عيادة د. محمود"))

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        settingsManager = AppSettingsManager(
            FakeHomeSettingsRepo,
            CoreConfig(baseUrl = "", isDebug = false, cryptoKeyAlias = "", defaultLanguage = "ar"),
        )
        viewModel = newViewModel()
    }

    private fun newViewModel() = HomeViewModel(
        FakeHomeSettingsRepo, settingsManager, LocaleHelper(settingsManager), FakeConnectivityObserver(connectivity), draftRepo, visitRepo, statisticsRepo, session.manager,
    )

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val state get() = viewModel.state.value

    @Test
    fun `missed visits are marked the first time home opens on a day`() {
        val today = startOfDay(System.currentTimeMillis())
        assertEquals(listOf(today), visitRepo.markMissedCalls)
        assertEquals(today, session.manager.user.value.missedCheckedDay)
    }

    @Test
    fun `opening home again the same day does not mark again`() {
        newViewModel()
        newViewModel()
        assertEquals(1, visitRepo.markMissedCalls.size)
    }

    @Test
    fun `a new day marks again and stores that day`() = runTest(dispatcher) {
        val today = startOfDay(System.currentTimeMillis())
        session.manager.updateUser { it.copy(missedCheckedDay = today - 24 * 3_600_000L) }
        newViewModel()
        assertEquals(listOf(today, today), visitRepo.markMissedCalls)
        assertEquals(today, session.manager.user.value.missedCheckedDay)
    }

    @Test
    fun `scheduled visits of earlier days become missed and today's stay scheduled`() = runTest(dispatcher) {
        val today = startOfDay(System.currentTimeMillis())
        val yesterday = Visit(id = "y", status = VisitStatus.Scheduled, dateTimeMillis = today - 3_600_000L)
        val later = Visit(id = "t", status = VisitStatus.Scheduled, dateTimeMillis = today + 3_600_000L)
        visitRepo.current.value = listOf(yesterday, later)
        session.manager.updateUser { it.copy(missedCheckedDay = 0L) }
        newViewModel()
        assertEquals(VisitStatus.Missed, visitRepo.get("y")!!.status)
        assertEquals(VisitStatus.Scheduled, visitRepo.get("t")!!.status)
    }

    @Test
    fun `the pending card follows the saved draft`() {
        assertNull(state.activeVisit)
        draftRepo.current.value = draft
        assertEquals("عيادة د. محمود", state.activeVisit?.placeName)
        draftRepo.current.value = null
        assertNull(state.activeVisit)
    }

    @Test
    fun `continue opens the plan visit screen on the saved draft`() = runTest(dispatcher) {
        draftRepo.current.value = draft
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnActiveContinue)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(resumeDraft = true))), events)
    }

    @Test
    fun `starting a visit with a pending one asks first and does not navigate`() = runTest(dispatcher) {
        draftRepo.current.value = draft
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnAddVisitClick)
        assertEquals(HomeOverlay.PendingVisit(HomeEvents.OnAddVisitClick), state.overlay)
        viewModel.onEvent(HomeEvents.OnOverlayDismiss)
        assertEquals(HomeOverlay.None, state.overlay)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.AddVisit))
        viewModel.onEvent(HomeEvents.OnStartVisit("p1"))
        assertEquals(HomeOverlay.PendingVisit(HomeEvents.OnStartVisit("p1")), state.overlay)
        assertEquals(0, events.size)
        assertEquals(0, draftRepo.discardCount)
    }

    @Test
    fun `confirming cancels the pending visit and continues to what was asked`() = runTest(dispatcher) {
        draftRepo.current.value = draft
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnStartVisit("p1"))
        viewModel.onEvent(HomeEvents.OnPendingVisitConfirm)
        assertEquals(1, draftRepo.discardCount)
        assertEquals(HomeOverlay.None, state.overlay)
        assertNull(state.activeVisit)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "p1"))), events)
    }

    @Test
    fun `cancel on the pending card asks, and confirming deletes the draft`() {
        draftRepo.current.value = draft
        viewModel.onEvent(HomeEvents.OnActiveCancel)
        assertEquals(HomeOverlay.DiscardPending, state.overlay)
        viewModel.onEvent(HomeEvents.OnOverlayDismiss)
        assertEquals(0, draftRepo.discardCount)
        viewModel.onEvent(HomeEvents.OnActiveCancel)
        viewModel.onEvent(HomeEvents.OnDiscardPendingConfirm)
        assertEquals(1, draftRepo.discardCount)
        assertEquals(HomeOverlay.None, state.overlay)
        assertNull(state.activeVisit)
    }

    @Test
    fun `connection starts offline and follows the network`() {
        assertEquals(ConnectionState.Offline, state.connection)
        assertFalse(state.shouldSync)

        connectivity.value = ConnectionState.Online
        assertEquals(ConnectionState.Online, state.connection)
        assertFalse(state.shouldSync) // nothing to upload

        visitRepo.current.value = listOf(Visit(id = "n", status = VisitStatus.Completed, syncStatus = SyncStatus.NotSynced))
        assertEquals(1, state.toSyncCount)
        assertTrue(state.shouldSync)

        connectivity.value = ConnectionState.Offline
        assertEquals(ConnectionState.Offline, state.connection)
        assertFalse(state.shouldSync)
    }

    @Test
    fun `initial state shows the session user, the saved street and today's date`() {
        assertEquals("Ahmed Mohamed", state.user.displayName)
        assertEquals("Rep Track", state.user.roleTitle)
        assertEquals("Union Square", state.address)
        assertEquals(startOfDay(System.currentTimeMillis()), startOfDay(state.dateMillis))
        assertNull(state.activeVisit)
        assertTrue(state.todayVisits.isEmpty())
        assertEquals(VisitProgress(), state.progress)
        assertEquals("ar", state.language)
        assertFalse(state.isDrawerOpen)
    }

    @Test
    fun `the user and the street follow the session`() = runTest(dispatcher) {
        session.manager.updateUser { it.copy(firstName = "Sara", lastAddress = null) }
        assertEquals("Sara Mohamed", state.user.displayName)
        assertNull(state.address)
    }

    @Test
    fun `today's visits are the scheduled ones of today, soonest first, at most three`() {
        val today = startOfDay(System.currentTimeMillis())
        fun visit(id: String, atHour: Int, status: VisitStatus = VisitStatus.Scheduled, dayOffset: Long = 0) =
            Visit(id = id, status = status, dateTimeMillis = today + dayOffset * 24 * HOUR + atHour * HOUR)
        visitRepo.current.value = listOf(
            visit("c", 14), visit("a", 9), visit("b", 11), visit("d", 16),
            visit("done", 8, VisitStatus.Completed), visit("tomorrow", 9, dayOffset = 1), visit("yesterday", 9, dayOffset = -1),
        )
        assertEquals(listOf("a", "b", "c"), state.todayVisits.map { it.id })
        val (from, to, limit) = visitRepo.todayRequests.single()
        assertEquals(today, from)
        assertEquals(startOfNextDay(today), to)
        assertEquals(3, limit)
    }

    @Test
    fun `the progress is asked for the Saturday to Friday week and follows the repo`() {
        val now = System.currentTimeMillis()
        assertEquals(listOf(startOfWeek(now) to startOfNextWeek(now)), statisticsRepo.requestedProgressRanges)
        statisticsRepo.progress.value = VisitProgress(completed = 10, planned = 26)
        assertEquals(10, state.progress.completed)
        assertEquals(26, state.progress.planned)
        assertEquals(38, state.progress.percent)
    }

    @Test
    fun `quick actions open planning, samples and gifts and accounts, and view all opens my visits`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnPlanVisitsClick)
        viewModel.onEvent(HomeEvents.OnSamplesAndGiftsClick)
        viewModel.onEvent(HomeEvents.OnAccountsClick)
        viewModel.onEvent(HomeEvents.OnTodayMoreClick)
        assertEquals(
            listOf<UiEvent>(
                UiEvent.Navigate(MainGraph.VisitPlanning),
                UiEvent.Navigate(MainGraph.SampleAndGifts),
                UiEvent.Navigate(MainGraph.Accounts),
                UiEvent.Navigate(MainGraph.VisitLog),
            ),
            events,
        )
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
    fun `the sync button does nothing yet`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnSyncClick)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `starting a visit opens the visit screen with its id`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnStartVisit("today-1"))
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.PlanVisit(visitId = "today-1"))), events)
    }

    @Test
    fun `add visit action and drawer row open the unplanned visit screen`() = runTest(dispatcher) {
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
    fun `orders drawer row sits under products and opens the orders screen`() = runTest(dispatcher) {
        val menu = NavDrawerItem.menu
        assertEquals(menu.indexOf(NavDrawerItem.Products) + 1, menu.indexOf(NavDrawerItem.Orders))
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Orders))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.Orders)), events)
    }

    @Test
    fun `statistics drawer row sits under orders and opens the statistics screen`() = runTest(dispatcher) {
        val menu = NavDrawerItem.menu
        assertEquals(menu.indexOf(NavDrawerItem.Orders) + 1, menu.indexOf(NavDrawerItem.Statistics))
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Statistics))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.Statistics)), events)
    }

    @Test
    fun `accounts drawer row opens the accounts screen`() = runTest(dispatcher) {
        val menu = NavDrawerItem.menu
        assertEquals(menu.indexOf(NavDrawerItem.Statistics) + 1, menu.indexOf(NavDrawerItem.Accounts))
        val events = collectEvents()
        viewModel.onEvent(HomeEvents.OnMenuClick)
        viewModel.onEvent(HomeEvents.OnDrawerItem(NavDrawerItem.Accounts))
        assertFalse(state.isDrawerOpen)
        assertEquals(listOf<UiEvent>(UiEvent.Navigate(MainGraph.Accounts)), events)
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

private const val HOUR = 3_600_000L

private object FakeHomeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}

private class FakeConnectivityObserver(private val flow: MutableStateFlow<ConnectionState>) : ConnectivityObserver {
    override val connection: Flow<ConnectionState> = flow
}
