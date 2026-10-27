package com.example.medicalrep.ui.screens.statistics.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.repo.FakeStatisticsRepo
import com.example.medicalrep.ui.screens.statistics.utils.StatisticsFakes
import com.example.medicalrep.ui.utils.startOfNextWeek
import com.example.medicalrep.ui.utils.startOfWeek
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. The screen starts at zeros and follows the repo's numbers.
 * 1b. It asks the repo for this week: from the Saturday at midnight to the next Saturday.
 * 2. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val repo = FakeStatisticsRepo()
    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = StatisticsViewModel(FakeSettingsRepo, repo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts at zeros and follows the repo`() {
        assertEquals(Statistics(), viewModel.state.value.statistics)
        repo.current.value = StatisticsFakes.state().statistics
        assertEquals(10, viewModel.state.value.statistics.completedVisits)
        assertEquals(26, viewModel.state.value.statistics.plannedVisits)
        repo.current.value = repo.current.value.copy(completedVisits = 11)
        assertEquals(11, viewModel.state.value.statistics.completedVisits)
    }

    @Test
    fun `asks for this week from the Saturday to the next Saturday`() {
        val (from, to) = repo.requestedRanges.single()
        val now = System.currentTimeMillis()
        assertEquals(startOfWeek(now), from)
        assertEquals(startOfNextWeek(now), to)
        assertTrue(from <= now && now < to)
        assertTrue(to - from in (7 * 24 - 1) * HOUR..(7 * 24 + 1) * HOUR)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(StatisticsEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }
}

private const val HOUR = 3_600_000L

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
