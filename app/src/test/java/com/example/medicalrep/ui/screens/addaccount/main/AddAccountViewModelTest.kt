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
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.FakeAccountRepo
import com.example.medicalrep.domain.repo.FakeLastLocationRepo
import com.example.medicalrep.ui.common.location.LocationStatus
import com.appcore.core.ui.utils.document_picker.PickedDocument
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the form is empty, the options come from the stored accounts and Save is not possible.
 *    The map starts at the rep's location when it is known.
 * 2. When fields / class / pin / photo change, Then the state follows; placing the pin again moves it.
 * 3. Save is impossible without a photo, a pin or a non-blank name (whitespace-only counts as blank).
 * 4. When Save while incomplete, Then nothing is sent.
 * 5. When Back, Then NavigationUp.
 * 6. When Save with everything filled, Then one account is stored (not uploaded) with the form values and the pin,
 *    a snackbar shows and the screen closes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddAccountViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val repo = FakeAccountRepo(
        listOf(
            Account("a1", name = "عيادة د. محمود", city = "منوف", district = "وسط البلد", entityType = "عيادة", specialty = "باطنة"),
            Account("a2", name = "صيدلية الشفاء", city = "قويسنا", district = "البر الشرقى", entityType = "صيدلية", specialty = "صيدلة"),
        ),
    )
    private val lastLocationRepo = FakeLastLocationRepo(GeoPoint(30.1, 31.2))
    private lateinit var viewModel: AddAccountViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AddAccountViewModel(FakeSettingsRepo, repo, lastLocationRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts empty with options and cannot save`() {
        assertEquals("", state.city)
        assertTrue(state.nameBlank)
        assertEquals(null, state.pin)
        assertEquals(null, state.signPhoto)
        assertEquals(listOf("A", "B", "C"), state.classOptions)
        assertEquals(listOf("منوف", "قويسنا"), state.cityOptions)
        assertEquals(listOf("وسط البلد", "البر الشرقى"), state.districtOptions)
        assertEquals(listOf("عيادة", "صيدلية"), state.entityTypeOptions)
        assertEquals(listOf("باطنة", "صيدلة"), state.specialtyOptions)
        assertEquals(30.1, state.userLocation.latitude, 0.0)
        assertFalse(state.canSave)
    }

    @Test
    fun `options follow the stored accounts`() {
        repo.current.value = repo.current.value + Account("a3", name = "مركز", city = "الباجور", district = "x", entityType = "مركز طبي", specialty = "عظام")
        assertTrue("الباجور" in state.cityOptions)
        assertTrue("مركز طبي" in state.entityTypeOptions)
    }

    @Test
    fun `without a location the map keeps its default start`() {
        viewModel = AddAccountViewModel(FakeSettingsRepo, repo, FakeLastLocationRepo())
        assertEquals(AddAccountState().userLocation, state.userLocation)
    }

    @Test
    fun `save with everything filled stores the account and closes`() = runTest(dispatcher) {
        val events = collectEvents()
        fillAllExceptName()
        state.name.edit { append("  Dr Ali ") }
        viewModel.onEvent(AddAccountEvents.SignPhotoCaptured(PickedDocument(android.net.FakeUri(), "sign.png", 10L)))
        assertTrue(state.canSave)
        viewModel.onEvent(AddAccountEvents.Save)
        val saved = repo.current.value.single { it.name == "Dr Ali" }
        assertEquals(SyncStatus.NotSynced, saved.syncStatus)
        assertEquals(listOf("c", "d", "e", "A", "s"), listOf(saved.city, saved.district, saved.entityType, saved.classCategory, saved.specialty))
        assertEquals(30.0, saved.latitude, 0.0)
        assertEquals(31.0, saved.longitude, 0.0)
        assertTrue(saved.signPhoto != null)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[1])
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
