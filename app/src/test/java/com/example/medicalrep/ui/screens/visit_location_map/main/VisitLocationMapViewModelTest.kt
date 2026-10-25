package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.screens.visit_location_map.utils.VisitLocationMapFakes
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
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the user location is the fake (route args are not readable in JVM tests).
 * 2. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitLocationMapViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: VisitLocationMapViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = VisitLocationMapViewModel(
            FakeSettingsRepo,
            SavedStateHandle(mapOf("visitLatitude" to 30.04, "visitLongitude" to 31.23)),
        )
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with the fake user location`() {
        val state = viewModel.state.value
        assertEquals(VisitLocationMapFakes.userLocation, state.userLocation)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(VisitLocationMapEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
