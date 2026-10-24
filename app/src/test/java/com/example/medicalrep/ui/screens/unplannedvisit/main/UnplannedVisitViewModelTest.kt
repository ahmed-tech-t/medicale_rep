package com.example.medicalrep.ui.screens.unplannedvisit.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
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
class UnplannedVisitViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: UnplannedVisitViewModel

    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = UnplannedVisitViewModel(FakeUnplannedSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts on entity step with sample visit`() {
        assertEquals(UnplannedVisitStep.Entity, state.currentStep)
        assertEquals(UnplannedVisitOverlay.None, state.overlay)
        assertEquals("شبين الكوم", state.visit.city)
        assertEquals(1, state.giftsCount)
        assertEquals(3, state.productsCount)
    }

    @Test
    fun `next and previous move between steps within bounds`() {
        viewModel.onEvent(UnplannedVisitEvents.Previous)
        assertEquals(UnplannedVisitStep.Entity, state.currentStep)

        viewModel.onEvent(UnplannedVisitEvents.Next)
        assertEquals(UnplannedVisitStep.GiftsProducts, state.currentStep)
        viewModel.onEvent(UnplannedVisitEvents.Next)
        assertEquals(UnplannedVisitStep.Result, state.currentStep)
        viewModel.onEvent(UnplannedVisitEvents.Next)
        assertEquals(UnplannedVisitStep.Result, state.currentStep)

        viewModel.onEvent(UnplannedVisitEvents.Previous)
        assertEquals(UnplannedVisitStep.GiftsProducts, state.currentStep)
    }

    @Test
    fun `entity selections update the visit`() {
        viewModel.onEvent(UnplannedVisitEvents.SelectCity("منوف"))
        viewModel.onEvent(UnplannedVisitEvents.SelectDistrict("وسط البلد"))
        viewModel.onEvent(UnplannedVisitEvents.SelectEntityType("صيدلية"))
        viewModel.onEvent(UnplannedVisitEvents.SelectPlace("مستشفى الجامعة"))
        viewModel.onEvent(UnplannedVisitEvents.SelectSpecialty("أطفال"))
        viewModel.onEvent(UnplannedVisitEvents.SelectSupervisor("د. سارة أحمد"))
        viewModel.onEvent(UnplannedVisitEvents.SelectShift(Shift.Pm))
        viewModel.onEvent(UnplannedVisitEvents.SelectDoubleVisit(false))

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
        viewModel.onEvent(UnplannedVisitEvents.OpenAddItem(ProductType.Gift))
        assertEquals(UnplannedVisitOverlay.AddItem(ProductType.Gift), state.overlay)
        viewModel.onEvent(UnplannedVisitEvents.DismissOverlay)
        assertEquals(UnplannedVisitOverlay.None, state.overlay)
    }

    @Test
    fun `confirm appends new items and merges existing ones`() {
        val notebook = UnplannedVisitFakes.giftCatalog[1].copy(quantity = 2)
        val mug = UnplannedVisitFakes.giftCatalog[0].copy(quantity = 1)

        viewModel.onEvent(UnplannedVisitEvents.OpenAddItem(ProductType.Gift))
        viewModel.onEvent(UnplannedVisitEvents.ConfirmAddItems(listOf(notebook, mug)))

        assertEquals(UnplannedVisitOverlay.None, state.overlay)
        assertEquals(listOf("gift-mug", "gift-notebook"), state.visit.gifts.map { it.id })
        assertEquals(2, state.visit.gifts.first { it.id == "gift-mug" }.quantity)
        assertEquals(4, state.giftsCount)
    }

    @Test
    fun `confirm with empty list keeps sheet open and visit unchanged`() {
        viewModel.onEvent(UnplannedVisitEvents.OpenAddItem(ProductType.Product))
        val before = state.visit
        viewModel.onEvent(UnplannedVisitEvents.ConfirmAddItems(emptyList()))
        assertEquals(UnplannedVisitOverlay.AddItem(ProductType.Product), state.overlay)
        assertEquals(before, state.visit)
    }

    @Test
    fun `confirm without open sheet is ignored`() {
        val before = state
        viewModel.onEvent(UnplannedVisitEvents.ConfirmAddItems(UnplannedVisitFakes.giftCatalog))
        assertEquals(before, state)
    }

    @Test
    fun `quantity is capped at remaining custody`() {
        repeat(10) { viewModel.onEvent(UnplannedVisitEvents.ChangeQuantity(ProductType.Gift, "gift-mug", +1)) }
        assertEquals(5, state.visit.gifts.single().quantity)
    }

    @Test
    fun `decreasing to zero removes the row`() {
        viewModel.onEvent(UnplannedVisitEvents.ChangeQuantity(ProductType.Product, "product-pediamol", -1))
        assertEquals(listOf("product-gastro"), state.visit.products.map { it.id })
    }

    @Test
    fun `clinic open checkbox follows toggle`() {
        viewModel.onEvent(UnplannedVisitEvents.ToggleClinicOpen(false))
        assertEquals(false, state.clinicOpenConfirmed)
        viewModel.onEvent(UnplannedVisitEvents.ToggleClinicOpen(true))
        assertEquals(true, state.clinicOpenConfirmed)
    }

    @Test
    fun `back navigates up and end visit shows snackbar then navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(UnplannedVisitEvents.Back)
        viewModel.onEvent(UnplannedVisitEvents.EndVisit)
        assertEquals(3, events.size)
        assertEquals(UiEvent.NavigationUp, events[0])
        assertTrue(events[1] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[2])
    }

    @Test
    fun `unbuilt result actions each show one snackbar`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(UnplannedVisitEvents.VoiceNoteClick)
        viewModel.onEvent(UnplannedVisitEvents.AttachClick)
        viewModel.onEvent(UnplannedVisitEvents.ViewLocationClick)
        assertEquals(3, events.size)
        assertTrue(events.all { it is UiEvent.ShowSnackBar })
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
