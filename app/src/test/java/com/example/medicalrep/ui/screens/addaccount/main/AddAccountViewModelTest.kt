package com.example.medicalrep.ui.screens.addaccount.main

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the form is empty, options are loaded and Save is not possible.
 * 2. When fields / class / pin / photo change, Then the state follows; placing the pin again moves it.
 * 3. Save is impossible without a photo, a pin or a non-blank name (whitespace-only counts as blank).
 * 4. When Save while incomplete, Then nothing is sent.
 * 5. When Back, Then NavigationUp.
 *
 * Not covered here: capturing a photo and the complete Save path. They need an android.net.Uri, which is
 * a null stub in JVM tests, so they belong in the instrumented tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddAccountViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AddAccountViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AddAccountViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts empty with options and cannot save`() {
        assertEquals("", state.city)
        assertTrue(state.nameBlank)
        assertEquals(null, state.pin)
        assertEquals(null, state.signPhoto)
        assertTrue(state.classOptions.isNotEmpty())
        assertTrue(state.cityOptions.isNotEmpty())
        assertFalse(state.canSave)
    }

    @Test
    fun `selections and pin are stored and the pin can move`() {
        viewModel.onEvent(AddAccountEvents.SelectCity("c"))
        viewModel.onEvent(AddAccountEvents.SelectDistrict("d"))
        viewModel.onEvent(AddAccountEvents.SelectEntityType("e"))
        viewModel.onEvent(AddAccountEvents.SelectClass("A"))
        viewModel.onEvent(AddAccountEvents.SelectSpecialty("s"))
        assertEquals(listOf("c", "d", "e", "A", "s"),
            listOf(state.city, state.district, state.entityType, state.classCategory, state.specialty))

        viewModel.onEvent(AddAccountEvents.PinPlaced(30.0, 31.0))
        viewModel.onEvent(AddAccountEvents.PinPlaced(30.5, 31.5))
        assertEquals(30.5, state.pin?.latitude ?: 0.0, 0.0)
        assertEquals(31.5, state.pin?.longitude ?: 0.0, 0.0)
    }

    @Test
    fun `save is impossible without a photo even when everything else is filled`() {
        fillAllExceptName()
        state.name.edit { append("Dr Ali") }
        assertTrue(state.pin != null)
        assertFalse(state.canSave)
    }

    @Test
    fun `a whitespace only name counts as blank`() {
        state.name.edit { append("   ") }
        assertTrue(state.nameBlank)
        state.name.edit { replace(0, length, "Dr Ali") }
        assertFalse(state.nameBlank)
    }

    @Test
    fun `save while incomplete sends nothing`() = runTest(dispatcher) {
        val events = collectEvents()
        fillAllExceptName()
        viewModel.onEvent(AddAccountEvents.Save)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(AddAccountEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    private fun fillAllExceptName() { // everything except the name and the photo
        viewModel.onEvent(AddAccountEvents.SelectCity("c"))
        viewModel.onEvent(AddAccountEvents.SelectDistrict("d"))
        viewModel.onEvent(AddAccountEvents.SelectEntityType("e"))
        viewModel.onEvent(AddAccountEvents.SelectClass("A"))
        viewModel.onEvent(AddAccountEvents.SelectSpecialty("s"))
        viewModel.onEvent(AddAccountEvents.PinPlaced(30.0, 31.0))
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
