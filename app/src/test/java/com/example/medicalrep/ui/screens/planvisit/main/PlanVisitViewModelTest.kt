package com.example.medicalrep.ui.screens.planvisit.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.FakeSession
import com.example.medicalrep.ui.common.location.LocationStatus
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.navigation.model.MainGraph
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.repo.FakeAccountRepo
import com.example.medicalrep.domain.repo.FakeProductRepo
import com.example.medicalrep.domain.repo.FakeRepRepo
import com.example.medicalrep.domain.repo.FakeVisitRepo
import com.example.medicalrep.domain.repo.FakeDraftVisitRepo
import com.appcore.core.ui.utils.document_picker.PickedDocument
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
 * 1. Given a new visit, Then it starts on the entity step with the Figma sample data and no overlay.
 * 2. Given entity step, When Next twice, Then result step; When Next again, Then it stays (upper bound).
 *    When Previous on entity step, Then it stays (lower bound).
 * 3. Given entity step, When city/district/type/place/supervisor/shift/visit type change,
 *    Then the visit reflects each selection.
 * 4. Given gifts step, When add gift is opened, Then overlay is AddItem(Gift); dismiss closes it.
 * 5. Given the add-item sheet, When confirming a new item, Then it is appended and the sheet closes;
 *    When confirming an existing item, Then its quantity is merged (no duplicate row).
 * 6. Given the add-item sheet, When confirming an empty list, Then nothing changes and the sheet stays open.
 * 7. Given no overlay, When ConfirmAddItems arrives, Then it is ignored.
 * 8. Given a gift row, When + is tapped beyond remaining custody, Then quantity is capped at remaining.
 * 9. Given a row with quantity 1, When − is tapped, Then the row is removed.
 * 10. Given the result step, When the clinic checkbox toggles, Then state follows.
 * 11. When Back, Then NavigationUp. When EndVisit, Then the visit is stored, the draft cleared, then snackbar then NavigationUp.
 * 20. An unplanned visit is a new completed row: account from the chosen place, times, duration, not uploaded; the rep id is the signed-in rep's;
 *     the supervisor id only for a double visit.
 * 20b. A visit that was stored before (a planned one) keeps its server id and request times when it is completed.
 * 21. When storing fails, Then the draft stays, an error snackbar shows and the screen does not close; ending again can succeed.
 * 22. Given a save is running, When EndVisit arrives again, Then it is ignored (stored once).
 * 13. Given the visit moves to the result step, Then the location is captured once from the session: coordinates, the time of the fix and address.
 * 19. Given the saved fix is worse than 20 m (or has no accuracy), Then the result step shows the weak signal warning; a good fix shows none.
 * 14. Given the result step was reached before, When it is reached again, Then the location is not captured again.
 * 16. Given no location is available, Then a snackbar shows and the old coordinates are kept.
 * 17. Given the address lookup fails, Then the address is null and the coordinates are kept.
 * 12. When view location, Then the map opens with the visit coordinates (no coordinates: see VisitDistanceTest).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlanVisitViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: PlanVisitViewModel
    private val session = FakeSession().also {
        it.setLocation(GeoPoint(30.55, 31.13))
        it.setAddress("Al Geish Street")
    }
    private val draftRepo = FakeDraftVisitRepo()
    private val accountRepo = FakeAccountRepo(
        listOf(
            Account(
                "a1", name = "مستشفى الجامعة", city = "منوف", district = "وسط البلد", entityType = "صيدلية", specialty = "أطفال",
                classCategory = "A", address = "برج الأطباء", latitude = 30.1444, longitude = 31.2357,
            ),
            Account("a2", name = "عيادة د. هاني", city = "قويسنا", district = "البر الشرقى", entityType = "عيادة", specialty = "باطنة"),
        ),
    )
    private val repRepo = FakeRepRepo(listOf(Rep("s1", "د. سارة أحمد", role = RepRole.Supervisor)))
    private val productRepo = FakeProductRepo(
        custody = listOf(
            Custody("gift-mug", "مج حراري", ProductType.Gift, received = 8, dispatched = 3),
            Custody("gift-notebook", "دفتر ملاحظات", ProductType.Gift, received = 12),
            Custody("sample-1", "GastroCure", ProductType.Product, received = 20),
        ),
    )
    private val visitRepo = FakeVisitRepo()

    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = createViewModel()
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Fills every search field of step 1; the place's account also sets the class, address and coordinates. */
    private fun fillEntityStep() {
        viewModel.onEvent(PlanVisitEvents.SelectCity("منوف"))
        viewModel.onEvent(PlanVisitEvents.SelectDistrict("وسط البلد"))
        viewModel.onEvent(PlanVisitEvents.SelectEntityType("صيدلية"))
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مستشفى الجامعة"))
        viewModel.onEvent(PlanVisitEvents.SelectSupervisor("د. سارة أحمد"))
    }

    private fun addVoiceNote() = viewModel.onEvent(PlanVisitEvents.VoiceNoteRecorded(byteArrayOf(1, 2, 3)))

    private fun addImage() {
        viewModel.onEvent(PlanVisitEvents.AttachImageResult(PickedDocument(android.net.FakeUri(), "note.png", 10L)))
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        PlanVisitViewModel(
            FakeUnplannedSettingsRepo, savedStateHandle, session.manager, draftRepo, accountRepo, repRepo, productRepo, visitRepo,
        )

    @Test
    fun `back with no changes leaves at once`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
        assertEquals(PlanVisitOverlay.None, state.overlay)
    }

    @Test
    fun `back with changes asks to save and does not leave`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.SelectCity("Cairo"))
        viewModel.onEvent(PlanVisitEvents.Back)
        assertEquals(PlanVisitOverlay.SaveDraft, state.overlay)
        assertEquals(0, events.size)
    }

    @Test
    fun `back on the result step leaves without asking even with changes`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.SelectCity("Cairo"))
        openResultStep()
        viewModel.onEvent(PlanVisitEvents.Back)
        assertEquals(PlanVisitOverlay.None, state.overlay)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    @Test
    fun `save stores the draft with the current step, shows a snackbar and leaves`() = runTest(dispatcher) {
        val events = collectEvents()
        fillEntityStep()
        viewModel.onEvent(PlanVisitEvents.Next)
        viewModel.onEvent(PlanVisitEvents.Back)
        viewModel.onEvent(PlanVisitEvents.SaveDraft)
        val saved = draftRepo.current.value!!
        assertEquals(1, saved.step)
        assertEquals("منوف", saved.visit.city)
        assertEquals(PlanVisitOverlay.None, state.overlay)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[1])
    }

    @Test
    fun `dont save discards the draft and leaves`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.SelectCity("Cairo"))
        viewModel.onEvent(PlanVisitEvents.Back)
        viewModel.onEvent(PlanVisitEvents.DiscardDraft)
        assertEquals(1, draftRepo.discardCount)
        assertNull(draftRepo.current.value)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    @Test
    fun `cancel closes the dialog and stays in the form`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.SelectCity("Cairo"))
        viewModel.onEvent(PlanVisitEvents.Back)
        viewModel.onEvent(PlanVisitEvents.DismissOverlay)
        assertEquals(PlanVisitOverlay.None, state.overlay)
        assertEquals(0, events.size)
        assertEquals("Cairo", state.visit.city)
    }

    @Test
    fun `a saved draft fills the form at its step and counts as unchanged`() {
        val resumed = PlanVisitState().withDraft(DraftVisit(step = 1, visit = Visit(city = "Giza", placeName = "د. سامي")))
        assertEquals(PlanVisitStep.GiftsProducts, resumed.currentStep)
        assertEquals("Giza", resumed.visit.city)
        assertEquals("د. سامي", resumed.visit.placeName)
        assertEquals(resumed.visit, resumed.initialVisit)
        assertEquals(false, resumed.asksToSave)
    }

    @Test
    fun `a draft step outside the form is clamped`() {
        assertEquals(PlanVisitStep.Result, PlanVisitState().withDraft(DraftVisit(step = 9, visit = Visit())).currentStep)
        assertEquals(PlanVisitStep.Entity, PlanVisitState().withDraft(DraftVisit(step = -3, visit = Visit())).currentStep)
    }

    @Test
    fun `ending the visit also clears any saved draft`() = runTest(dispatcher) {
        draftRepo.current.value = DraftVisit(0, Visit())
        addVoiceNote()
        addImage()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertNull(draftRepo.current.value)
    }

    private fun readyToEnd() {
        openResultStep()
        addVoiceNote()
        addImage()
    }

    @Test
    fun `ending an unplanned visit stores one completed row for the chosen account`() = runTest(dispatcher) {
        readyToEnd()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        val saved = visitRepo.current.value.single()
        assertEquals(state.visit.id, saved.id)
        assertEquals(VisitType.Unplanned, saved.type)
        assertEquals(VisitStatus.Completed, saved.status)
        assertEquals(SyncStatus.NotSynced, saved.syncStatus)
        assertEquals("a1", saved.accountId)
        assertEquals(30.55, saved.visitLatitude, 0.0)
        assertTrue(saved.dateTimeMillis!! > 0 && saved.completedAtMillis!! >= saved.dateTimeMillis!!)
        assertTrue(Regex("\\d\\d:\\d\\d د").matches(saved.durationLabel.orEmpty()))
        assertNotNull(saved.voiceNote)
        assertNotNull(saved.imageNote)
        assertNull(saved.supervisorId)
        assertEquals(session.manager.user.value.id.toString(), saved.repId)
    }

    @Test
    fun `ending a visit that was stored before keeps its server id and request times`() = runTest(dispatcher) {
        visitRepo.current.value = listOf(Visit(id = state.visit.id, backendId = "srv-9", requestedAtMillis = 5L, decidedAtMillis = 6L))
        readyToEnd()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        val saved = visitRepo.current.value.single()
        assertEquals("srv-9", saved.backendId)
        assertEquals(5L, saved.requestedAtMillis)
        assertEquals(6L, saved.decidedAtMillis)
        assertEquals(VisitStatus.Completed, saved.status)
    }

    @Test
    fun `the visit always carries the signed-in rep's id, whether or not he is in the reps table`() = runTest(dispatcher) {
        readyToEnd()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertEquals(session.manager.user.value.id.toString(), visitRepo.current.value.single().repId)
        assertNull(repRepo.get(session.manager.user.value.id.toString()))
    }

    @Test
    fun `a double visit stores the chosen supervisor`() = runTest(dispatcher) {
        viewModel.onEvent(PlanVisitEvents.SelectDoubleVisit(true))
        readyToEnd()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        val saved = visitRepo.current.value.single()
        assertEquals(VisitMode.Double, saved.visitMode)
        assertEquals("s1", saved.supervisorId)
    }

    @Test
    fun `a failed save keeps the draft shows an error and does not close, and a retry works`() = runTest(dispatcher) {
        val events = collectEvents()
        draftRepo.current.value = DraftVisit(0, Visit())
        readyToEnd()
        visitRepo.failOnSave = true
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertTrue(visitRepo.current.value.isEmpty())
        assertNotNull(draftRepo.current.value)
        assertEquals(1, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
        assertFalse(state.isSaving)

        visitRepo.failOnSave = false
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertEquals(1, visitRepo.current.value.size)
        assertNull(draftRepo.current.value)
        assertEquals(UiEvent.NavigationUp, events.last())
    }

    @Test
    fun `tapping end twice while saving stores the visit once`() = runTest(dispatcher) {
        val events = collectEvents()
        readyToEnd()
        val gate = CompletableDeferred<Unit>()
        visitRepo.saveGate = gate
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertTrue(state.isSaving)
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        gate.complete(Unit)
        assertEquals(1, visitRepo.current.value.size)
        assertEquals(1, events.count { it == UiEvent.NavigationUp })
    }

    @Test
    fun `choosing a place stores its account id on the visit`() {
        fillEntityStep()
        assertEquals("a1", state.visit.accountId)
    }

    private fun openResultStep() {
        fillEntityStep()
        viewModel.onEvent(PlanVisitEvents.Next)
        viewModel.onEvent(PlanVisitEvents.Next)
    }

    @Test
    fun `result step captures coordinates, time and address once`() {
        openResultStep()
        assertEquals(30.55, state.visit.visitLatitude, 0.0)
        assertEquals(31.13, state.visit.visitLongitude, 0.0)
        assertEquals(session.manager.user.value.lastLocationTimeMillis, state.capturedAtMillis)
        assertEquals("Al Geish Street", state.locationAddress)
        val capturedAt = state.capturedAtMillis

        viewModel.onEvent(PlanVisitEvents.Previous)
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(capturedAt, state.capturedAtMillis)
    }

    @Test
    fun `a fix worse than 20 m raises the weak signal warning`() {
        session.setLocation(GeoPoint(30.55, 31.13), accuracyMeters = 40f)
        openResultStep()
        assertEquals(40f, state.locationAccuracyMeters)
        assertTrue(state.hasWeakSignal)
    }

    @Test
    fun `a good fix shows no warning`() {
        session.setLocation(GeoPoint(30.55, 31.13), accuracyMeters = 12f)
        openResultStep()
        assertFalse(state.hasWeakSignal)
    }

    @Test
    fun `exactly 20 m is still good and no location shows no warning`() {
        session.setLocation(GeoPoint(30.55, 31.13), accuracyMeters = 20f)
        openResultStep()
        assertFalse(state.hasWeakSignal)
        assertFalse(PlanVisitState().hasWeakSignal)
    }

    @Test
    fun `no location shows a snackbar and keeps the old coordinates`() = runTest(dispatcher) {
        val events = collectEvents()
        val before = state.visit.visitLatitude
        session.setLocation(null)
        openResultStep()
        assertEquals(before, state.visit.visitLatitude, 0.0)
        assertNull(state.capturedAtMillis)
        assertTrue(events.any { it is UiEvent.ShowSnackBar })
    }

    @Test
    fun `no saved address keeps coordinates and shows no address`() {
        session.setAddress(null)
        openResultStep()
        assertNull(state.locationAddress)
        assertEquals(30.55, state.visit.visitLatitude, 0.0)
    }

    @Test
    fun `no visit id opens an editable unplanned visit`() {
        assertEquals(VisitType.Unplanned, state.visit.type)
    }

    @Test
    fun `visit id opens a planned visit carrying that id`() {
        val planned = createViewModel(SavedStateHandle(mapOf("visitId" to "v-42")))
        assertEquals("v-42", planned.state.value.visit.id)
        assertEquals(VisitType.Planned, planned.state.value.visit.type)
    }

    @Test
    fun `a new visit starts on the entity step with the search fields empty and the options from Room`() {
        assertEquals(PlanVisitStep.Entity, state.currentStep)
        assertEquals(PlanVisitOverlay.None, state.overlay)
        with(state.visit) {
            listOf(city, region, account, placeName, specialty, supervisorName, classCategory, address)
                .forEach { assertEquals("", it) }
            assertEquals(VisitType.Unplanned, type)
            assertTrue(date.isNotBlank() && time.isNotBlank())
        }
        assertEquals(listOf("منوف", "قويسنا"), state.cityOptions)
        assertEquals(listOf("وسط البلد", "البر الشرقى"), state.districtOptions)
        assertEquals(listOf("صيدلية", "عيادة"), state.entityTypeOptions)
        assertEquals(listOf("د. سارة أحمد"), state.supervisorOptions)
        assertEquals(listOf("gift-mug", "gift-notebook"), state.giftCatalog.map { it.id })
        assertEquals(listOf("sample-1"), state.productCatalog.map { it.id })
        assertEquals(5, state.giftCatalog.first().remaining)
        assertEquals(0, state.giftsCount)
        assertEquals(0, state.productsCount)
        assertEquals(false, state.isEntityComplete)
    }

    @Test
    fun `places are the accounts at the chosen city, district and type`() {
        assertEquals(listOf("مستشفى الجامعة", "عيادة د. هاني"), state.placeOptions)
        viewModel.onEvent(PlanVisitEvents.SelectCity("منوف"))
        assertEquals(listOf("مستشفى الجامعة"), state.placeOptions)
        viewModel.onEvent(PlanVisitEvents.SelectEntityType("عيادة"))
        assertEquals(emptyList<String>(), state.placeOptions)
    }

    @Test
    fun `choosing a place fills its class, address and coordinates`() {
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مستشفى الجامعة"))
        assertEquals("A", state.visit.classCategory)
        assertEquals("برج الأطباء", state.visit.address)
        assertEquals(30.1444, state.visit.accountLatitude, 0.0)
        assertEquals(31.2357, state.visit.accountLongitude, 0.0)
    }

    @Test
    fun `choosing a place fills the specialty from its account and the rep cannot change it`() {
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مستشفى الجامعة"))
        assertEquals("أطفال", state.visit.specialty)
        viewModel.onEvent(PlanVisitEvents.SelectPlace("عيادة د. هاني"))
        assertEquals("باطنة", state.visit.specialty)
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مكان غير موجود"))
        assertEquals("", state.visit.specialty)
    }

    @Test
    fun `step 1 does not need a specialty, so an account without one still goes on`() {
        accountRepo.current.value = accountRepo.current.value +
            Account("a3", name = "مركز بلا تخصص", city = "منوف", district = "وسط البلد", entityType = "صيدلية")
        viewModel.onEvent(PlanVisitEvents.SelectCity("منوف"))
        viewModel.onEvent(PlanVisitEvents.SelectDistrict("وسط البلد"))
        viewModel.onEvent(PlanVisitEvents.SelectEntityType("صيدلية"))
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مركز بلا تخصص"))
        assertEquals("", state.visit.specialty)
        assertEquals(true, state.canGoNext)
    }

    @Test
    fun `options follow the stored data`() {
        accountRepo.current.value = accountRepo.current.value + Account("a3", name = "مركز", city = "الباجور", district = "x", entityType = "مركز طبي", specialty = "عظام")
        assertTrue("الباجور" in state.cityOptions)
        repRepo.current.value = repRepo.current.value + Rep("s2", "د. وائل", role = RepRole.Supervisor)
        assertEquals(listOf("د. سارة أحمد", "د. وائل"), state.supervisorOptions)
    }

    @Test
    fun `next on step 1 is blocked until every field is filled`() {
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.Entity, state.currentStep)
        val fills = listOf(
            PlanVisitEvents.SelectCity("منوف"),
            PlanVisitEvents.SelectDistrict("وسط البلد"),
            PlanVisitEvents.SelectEntityType("صيدلية"),
            PlanVisitEvents.SelectPlace("مستشفى الجامعة"),
        )
        viewModel.onEvent(PlanVisitEvents.SelectDoubleVisit(true))
        fills.forEach { fill ->
            assertEquals(false, state.canGoNext)
            viewModel.onEvent(fill)
            // The supervisor is still missing for a double visit.
            assertEquals(false, state.canGoNext)
        }
        viewModel.onEvent(PlanVisitEvents.SelectSupervisor("د. سارة أحمد"))
        assertEquals(true, state.canGoNext)
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.GiftsProducts, state.currentStep)
    }

    @Test
    fun `a single visit does not need a supervisor`() {
        viewModel.onEvent(PlanVisitEvents.SelectDoubleVisit(false))
        viewModel.onEvent(PlanVisitEvents.SelectCity("منوف"))
        viewModel.onEvent(PlanVisitEvents.SelectDistrict("وسط البلد"))
        viewModel.onEvent(PlanVisitEvents.SelectEntityType("صيدلية"))
        viewModel.onEvent(PlanVisitEvents.SelectPlace("مستشفى الجامعة"))
        assertEquals(true, state.canGoNext)
        viewModel.onEvent(PlanVisitEvents.SelectDoubleVisit(true))
        assertEquals(false, state.canGoNext)
    }

    @Test
    fun `step 2 is optional and always allows next`() {
        fillEntityStep()
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(true, state.canGoNext)
        viewModel.onEvent(PlanVisitEvents.Next)
        assertEquals(PlanVisitStep.Result, state.currentStep)
    }

    @Test
    fun `the visit cannot end without both a voice note and an image`() = runTest(dispatcher) {
        openResultStep()
        val events = collectEvents()
        assertEquals(false, state.canEndVisit)
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        addVoiceNote()
        assertEquals(false, state.canEndVisit)
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertEquals(0, events.size)

        addImage()
        assertEquals(true, state.canEndVisit)
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[1])
    }

    @Test
    fun `an image alone does not allow ending and deleting the voice note blocks it again`() {
        openResultStep()
        addImage()
        assertEquals(false, state.canEndVisit)
        addVoiceNote()
        assertEquals(true, state.canEndVisit)
        viewModel.onEvent(PlanVisitEvents.VoiceNoteDeleted)
        assertEquals(false, state.canEndVisit)
    }

    @Test
    fun `next and previous move between steps within bounds`() {
        fillEntityStep()
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
        val notebook = state.giftCatalog[1].copy(quantity = 2)
        val mug = state.giftCatalog[0].copy(quantity = 1)
        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Gift))
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(listOf(mug)))

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
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(state.giftCatalog))
        assertEquals(before, state)
    }

    @Test
    fun `quantity is capped at remaining custody`() {
        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Gift))
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(listOf(state.giftCatalog.first { it.id == "gift-mug" })))
        repeat(10) { viewModel.onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Gift, "gift-mug", +1)) }
        assertEquals(5, state.visit.gifts.single().quantity)
    }

    @Test
    fun `decreasing to zero removes the row`() {
        viewModel.onEvent(PlanVisitEvents.OpenAddItem(ProductType.Product))
        viewModel.onEvent(PlanVisitEvents.ConfirmAddItems(state.productCatalog))
        assertEquals(listOf("sample-1"), state.visit.products.map { it.id })
        viewModel.onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Product, "sample-1", -1))
        assertEquals(emptyList<String>(), state.visit.products.map { it.id })
    }

    @Test
    fun `clinic open checkbox follows toggle`() {
        viewModel.onEvent(PlanVisitEvents.ToggleClinicOpen(false))
        assertEquals(false, state.visit.noIssueFound)
        viewModel.onEvent(PlanVisitEvents.ToggleClinicOpen(true))
        assertEquals(true, state.visit.noIssueFound)
    }

    @Test
    fun `back navigates up and end visit shows snackbar then navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.Back)
        addVoiceNote()
        addImage()
        viewModel.onEvent(PlanVisitEvents.EndVisit)
        assertEquals(3, events.size)
        assertEquals(UiEvent.NavigationUp, events[0])
        assertTrue(events[1] is UiEvent.ShowSnackBar)
        assertEquals(UiEvent.NavigationUp, events[2])
    }

    @Test
    fun `view location opens the map with the visit coordinates`() = runTest(dispatcher) {
        openResultStep()
        val events = collectEvents()
        viewModel.onEvent(PlanVisitEvents.ViewLocationClick)
        assertEquals(
            listOf<UiEvent>(
                UiEvent.Navigate(
                    MainGraph.VisitLocationMap(
                        visitLatitude = state.visit.visitLatitude,
                        visitLongitude = state.visit.visitLongitude,
                        accountLatitude = state.visit.accountLatitude,
                        accountLongitude = state.visit.accountLongitude,
                        travelSeconds = state.visit.travelSeconds,
                    )
                )
            ),
            events,
        )
    }


    @Test
    fun `view account directions opens the screen with the account coordinates and name`() = runTest(dispatcher) {
        val events = collectEvents()
        fillEntityStep()
        viewModel.onEvent(PlanVisitEvents.ViewAccountDirectionsClick)
        assertEquals(
            listOf<UiEvent>(
                UiEvent.Navigate(
                    MainGraph.AccountDirections(
                        state.visit.accountLatitude,
                        state.visit.accountLongitude,
                        state.visit.placeName,
                    )
                )
            ),
            events,
        )
    }

    @Test
    fun `view account location opens the screen with the account details`() = runTest(dispatcher) {
        val events = collectEvents()
        fillEntityStep()
        viewModel.onEvent(PlanVisitEvents.ViewAccountLocationClick)
        val visit = state.visit
        assertEquals(
            listOf<UiEvent>(
                UiEvent.Navigate(
                    MainGraph.AccountLocation(
                        accountLatitude = visit.accountLatitude,
                        accountLongitude = visit.accountLongitude,
                        accountName = visit.placeName,
                        address = "منوف - برج الأطباء",
                        classCategory = visit.classCategory,
                        accountType = visit.account,
                        specialty = visit.specialty,
                    )
                )
            ),
            events,
        )
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


