package com.example.medicalrep.ui.screens.visitdetails.main

import androidx.lifecycle.SavedStateHandle
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * 1. Given a route with a visit id, Then the state holds that visit.
 * 2. When View on map, Then one OpenUrl with the visit's geo uri (latitude, longitude).
 * 3. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitDetailsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: VisitDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitDetailsViewModel(FakeSettingsRepo, SavedStateHandle(mapOf("visitId" to "visit-7")))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `state holds the visit of the route id`() {
        assertEquals("visit-7", viewModel.state.value.visit.id)
    }

    @Test
    fun `view on map opens the visit location`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitDetailsEvents.ViewOnMap)
        val visit = viewModel.state.value.visit
        val geo = "geo:${visit.visitLatitude},${visit.visitLongitude}?q=${visit.visitLatitude},${visit.visitLongitude}"
        assertEquals(listOf<UiEvent>(UiEvent.OpenUrl(geo)), events)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(VisitDetailsEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
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
