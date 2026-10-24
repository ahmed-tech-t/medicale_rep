package com.example.medicalrep.ui.screens.planvisit.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD cases (Given / When / Then):
 * 1. Given a new visit, Then it starts on the entity step with the Figma sample data and no overlay.
 * 2. Given entity step, When Next twice, Then result step; When Next again, Then it stays (upper bound).
 *    When Previous on entity step, Then it stays (lower bound).
 * 3. Given entity step, When city/district/type/place/specialty/supervisor/shift/visit type change,
 *    Then the visit reflects each selection.
 * 4. Given gifts step, When add gift is opened, Then overlay is AddItem(Gift); dismiss closes it.
 * 5. Given the add-item sheet, When confirming a new item, Then it is appended and the sheet closes;
 *    When confirming an existing item, Then its quantity is merged (no duplicate row).
 * 6. Given the add-item sheet, When confirming an empty list, Then nothing changes and the sheet stays open.
 * 7. Given no overlay, When ConfirmAddItems arrives, Then it is ignored.
 * 8. Given a gift row, When + is tapped beyond remaining custody, Then quantity is capped at remaining.
 * 9. Given a row with quantity 1, When − is tapped, Then the row is removed.
 * 10. Given the result step, When the clinic checkbox toggles, Then state follows.
 * 11. When Back, Then NavigationUp. When EndVisit, Then snackbar then NavigationUp.
 * 12. When voice note / attach / view location, Then each sends one snackbar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlanVisitViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: PlanVisitViewModel

    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = PlanVisitViewModel(FakeUnplannedSettingsRepo, SavedStateHandle())
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `no visit id opens an editable unplanned visit`() {
        assertEquals(VisitType.Unplanned, state.visit.type)
    }

    @Test
    fun `visit id opens a planned visit carrying that id`() {
        val planned = PlanVisitViewModel(FakeUnplannedSettingsRepo, SavedStateHandle(mapOf("visitId" to "v-42")))
        assertEquals("v-42", planned.state.value.visit.id)
        assertEquals(VisitType.Planned, planned.state.value.visit.type)
    }

    @Test
    fun `starts on entity step with sample visit`() {
        assertEquals(PlanVisitStep.Entity, state.currentStep)
        assertEquals(PlanVisitOverlay.None, state.overlay)
        assertEquals("شبين الكوم", state.visit.city)
        assertEquals(1, state.giftsCount)
        assertEquals(3, state.productsCount)
    }

    @Test
    fun `next and previous move between steps within bounds`() {
        viewModel.onEvent(PlanVisitEvents.Previous)
        assertEquals(PlanVisitStep.Entity, state.currentStep)

        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.GiftsProducts, state.currentStep)
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.Result, state.currentStep)
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.Result, state.currentStep)

        viewModel.onEvent(PlanVisitEvents.Previous)
        assertEquals(PlanVisitStep.GiftsProducts, state.currentStep)
    }

    @Test
    fun `entity selections update the visit`() {
        viewModel.onEvent(PlanVisitEvents.SelectCity("منوف"))
        viewModel.onEvent(PlanVisitEvents.SelectDistrict("وسط البلد"))
        viewModel.onEvent(PlanVisitEvents.SelectEntityType("صيدلية"))
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مستشفى الجامعة"))
        viewModel.onEvent(PlanVisitEvents.SelectSpecialty("أطفال"))
        viewModel.onEvent(PlanVisitEvents.SelectSupervisor("د. سارة أحمد"))
        viewModel.onEvent(PlanVisitEvents.SelectShift(Shift.Pm))
        viewModel.onEvent(PlanVisitEvents.SelectDoubleVisit(false))

        with(state.visit) {
            assertEquals("منوف", city)
            assertEquals("وسط البلد", region)
            assertEquals("صيدلية", account)
            assertEquals("مستشفى الجامعة", placeName)
            assertEquals("أطفال", specialty)
            assertEquals("د. سارة أحمد", supervisorName)
            assertEquals(Shift.Pm, shift)
            assertEquals(false, isDoubleVisit)
        }
    }

    @Test
    fun `add item sheet opens for the kind and dismisses`() {
        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Gift))
        assertEquals(PlanVisitOverlay.AddItem(ProductType.Gift), state.overlay)
        viewModel.onEvent(PlanVisitEvents.DismissOverlay)
        assertEquals(PlanVisitOverlay.None, state.overlay)
    }

    @Test
    fun `confirm appends new items and merges existing ones`() {
        val notebook = PlanVisitFakes.giftCatalog[1].copy(quantity = 2)
        val mug = PlanVisitFakes.giftCatalog[0].copy(quantity = 1)

        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Gift))
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(listOf(notebook, mug)))

        assertEquals(PlanVisitOverlay.None, state.overlay)
        assertEquals(listOf("gift-mug", "gift-notebook"), state.visit.gifts.map { it.id })
        assertEquals(2, state.visit.gifts.first { it.id == "gift-mug" }.quantity)
        assertEquals(4, state.giftsCount)
    }

    @Test
    fun `confirm with empty list keeps sheet open and visit unchanged`() {
        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Product))
        val before = state.visit
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(emptyList()))
        assertEquals(PlanVisitOverlay.AddItem(ProductType.Product), state.overlay)
        assertEquals(before, state.visit)
    }

    @Test
    fun `confirm without open sheet is ignored`() {
        val before = state
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(PlanVisitFakes.giftCatalog))
        assertEquals(before, state)
    }

    @Test
    fun `quantity is capped at remaining custody`() {
        repeat(10) { viewModel.onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Gift, "gift-mug", +1)) }
        assertEquals(5, state.visit.gifts.single().quantity)
    }

    @Test
    fun `decreasing to zero removes the row`() {
        viewModel.onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Product, "product-pediamol", -1))
        assertEquals(listOf("product-gastro"), state.visit.products.map { it.id })
    }

    @Test
    fun `clinic open checkbox follows toggle`() {
        viewModel.onEvent(PlanVisitEvents.ToggleClinicOpen(false))
        assertEquals(false, state.clinicOpenConfirmed)
        viewModel.onEvent(PlanVisitEvents.ToggleClinicOpen(true))
        assertEquals(true, state.clinicOpenConfirmed)
    }

    @Test
    fun `back navigates up and end visit shows snackbar then navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.Back)
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertEquals(3, events.size)
        assertEquals(UiEvent.NavigationUp, events[0])
        assertTrue(events[1] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[2])
    }

    @Test
    fun `unbuilt result actions each show one snackbar`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.AttachClick)
        viewModel.onEvent(PlanVisitEvents.ViewLocationClick)
        assertEquals(2, events.size)
        assertTrue(events.all { it is UiEvent.ShowSnackBar })
    }

    @Test
    fun `recording started sets the recording flag`() {
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecordingStarted)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecordingStarted)
        assertTrue(state.isRecordingVoice)
    }

    @Test
    fun `recorded bytes are saved on the visit and clear the flag`() {
        val bytes = byteArrayOf(1, 2, 3)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecordingStarted)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(bytes))
        assertEquals(false, state.isRecordingVoice)
        assertTrue(bytes.contentEquals(state.visit.voiceNote))
    }

    @Test
    fun `too short recording clears the flag without replacing the saved note`() {
        val saved = byteArrayOf(9)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(saved))
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecordingStarted)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(null))
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(byteArrayOf()))
        assertEquals(false, state.isRecordingVoice)
        assertTrue(saved.contentEquals(state.visit.voiceNote))
    }

    @Test
    fun `deleting the voice note clears it so it can be re-recorded`() {
        viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(byteArrayOf(1)))
        viewModel.onEvent(PlanVisitEvents.VoiceNoteDeleted)
        assertEquals(null, state.visit.voiceNote)
    }

    @Test
    fun `microphone permission denied shows one snackbar`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.VoiceNotePermissionDenied)
        assertEquals(1, events.size)
        assertTrue(events.single() is UiEvent.ShowSnackBar)
    }

    private fun TestScope.collectEvents(): MutableList<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        return events
    }
}

private object FakeUnplannedSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
