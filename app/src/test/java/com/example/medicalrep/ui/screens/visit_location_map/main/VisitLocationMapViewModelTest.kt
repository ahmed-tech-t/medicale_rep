package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.common.location.LocationHelper
import com.example.medicalrep.ui.common.location.LocationStatus
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the helper returns a location, Then it is the user location.
 * 1b. Given the helper returns nothing (no permission, no GPS, no last location), Then the user location stays null.
 * 2. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VisitLocationMapViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: VisitLocationMapViewModel
    private val location = MapCameraTarget(latitude = 30.05, longitude = 31.24)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = createViewModel(location)
    }

    private fun createViewModel(userLocation: MapCameraTarget?) = VisitLocationMapViewModel(
        FakeSettingsRepo,
        SavedStateHandle(mapOf("visitLatitude" to 30.04, "visitLongitude" to 31.23)),
        FakeLocationHelper(userLocation),
    )

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `user location comes from the helper`() {
        assertEquals(location, viewModel.state.value.userLocation)
    }

    @Test
    fun `user location stays null when the helper has none`() {
        assertNull(createViewModel(null).state.value.userLocation)
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

private class FakeLocationHelper(private val location: MapCameraTarget?) : LocationHelper {
    override fun status() = LocationStatus.Ready
    override suspend fun getLocation() = location
}
