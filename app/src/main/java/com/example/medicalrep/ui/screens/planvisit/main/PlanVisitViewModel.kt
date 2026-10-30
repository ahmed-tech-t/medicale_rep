package com.example.medicalrep.ui.screens.planvisit.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiEvent.*
import com.appcore.core.domain.utils.UiText.*
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.models.WorkingTime
import com.example.medicalrep.domain.models.lastLocation
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.domain.repo.GeoRepo
import com.example.medicalrep.domain.repo.DoctorRepo
import com.example.medicalrep.domain.repo.DraftVisitRepo
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.domain.repo.RepRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.mapper.toCompletedVisit
import com.example.medicalrep.ui.mapper.toDomain
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitOverlay.*
import com.example.medicalrep.ui.screens.planvisit.utils.accountAddressLine
import com.example.medicalrep.ui.screens.planvisit.utils.toCatalogItem
import com.example.medicalrep.ui.screens.visitdetails.utils.hasAccountLocation
import com.example.medicalrep.ui.screens.visitdetails.utils.hasLocations
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanVisitViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val session: AuthSessionManager,
    private val draftRepo: DraftVisitRepo,
    private val accountRepo: AccountRepo,
    private val geoRepo: GeoRepo,
    private val doctorRepo: DoctorRepo,
    private val repRepo: RepRepo,
    productRepo: ProductRepo,
    private val visitRepo: VisitRepo,
) : BaseViewModel(appSettingsRepo) {

    /** Null means an unplanned visit: every field stays editable. */
    // The route's own argument keys: toRoute needs a real Bundle, which plain JVM tests do not have.
    private val visitId: String? = savedStateHandle.get<String>("visitId")
    private val isNewVisit: Boolean = savedStateHandle.get<Boolean>("isNew") ?: true
    private val resumeDraft: Boolean = savedStateHandle.get<Boolean>("resumeDraft") ?: false

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<PlanVisitState> = _state.asStateFlow()

    /** The accounts at the chosen city, district and type: the places the rep can pick. */
    private var accounts: List<Account> = emptyList()

    init {
        visitId?.let(::loadPlannedVisit)
        if (resumeDraft) loadDraft()
        collectInto(geoRepo.cities()) { state, cities -> state.copy(cityOptions = cities.map { it.toUi() }) }
        collectInto(accountRepo.entityTypes()) { state, types -> state.copy(entityTypeOptions = types.map { it.toUi() }) }
        viewModelScope.launch {
            repRepo.byRole(RepRole.Supervisor)
                .collect { list -> _state.update { it.copy(supervisors = list) } }
        }
        collectInto(productRepo.custodyList(ProductType.Gift)) { state, gifts ->
            state.copy(
                giftCatalog = gifts.map { it.toCatalogItem() })
        }
        collectInto(productRepo.custodyList(ProductType.Sample)) { state, samples ->
            state.copy(productCatalog = samples.map { it.toCatalogItem() })
        }
        viewModelScope.launch {
            _state.map { it.visit.city?.id }
                .distinctUntilChanged()
                .flatMapLatest { cityId -> geoRepo.districts(cityId) }
                .collect { districts -> _state.update { it.copy(districtOptions = districts.map { district -> district.toUi() }) } }
        }
        viewModelScope.launch {
            _state.map { it.visit.accountId }
                .distinctUntilChanged()
                .flatMapLatest { accountId -> if (accountId.isBlank()) flowOf(emptyList()) else doctorRepo.doctorsOf(accountId) }
                .collect { doctors ->
                    val options = doctors.map { doctor -> doctor.toUi() }
                    // A place with a single doctor (a clinic) needs no choice: its doctor is the visit's doctor.
                    _state.update { it.copy(doctorOptions = options, visit = if (options.size == 1) it.visit.copy(doctors = options) else it.visit) }
                }
        }
        viewModelScope.launch {
            _state.map { PlaceFilter(it.visit.city?.id, it.visit.region?.id, it.visit.account?.id) }
                .distinctUntilChanged()
                .flatMapLatest { filter -> accountRepo.places(filter.cityId, filter.districtId, filter.typeId) }
                .collect { places ->
                    accounts = places
                    _state.update { state ->
                        val chosenPlaceGone = state.visit.type == VisitType.Unplanned &&
                                state.visit.accountId.isNotBlank() &&
                                places.none { it.id == state.visit.accountId }
                        state.copy(
                            placeOptions = places.map { BasicModelUi(it.id, it.name) },
                            visit = if (chosenPlaceGone) state.visit.withoutPlace() else state.visit,
                        )
                    }
                }
        }
    }

    /** The accounts that fit the chosen city, district and entity type; a null id does not narrow the search. */
    private data class PlaceFilter(val cityId: String?, val districtId: String?, val typeId: String?)

    private fun <T> collectInto(flow: Flow<T>, transform: (PlanVisitState, T) -> PlanVisitState) {
        viewModelScope.launch { flow.collect { value -> _state.update { transform(it, value) } } }
    }

    /** A new visit starts empty, stamped with now; a planned visit carries its id until its data loads. */
    private fun initialState(): PlanVisitState {
        val openedAtMillis = System.currentTimeMillis()
        val visit = VisitUi(
            id = visitId ?: UUID.randomUUID().toString(),
            type = if (visitId == null) VisitType.Unplanned else VisitType.Planned,
            date = formatDate(openedAtMillis),
            time = formatClockTime(openedAtMillis),
        )
        return PlanVisitState(visit = visit, initialVisit = visit, openedAtMillis = openedAtMillis)
    }

    /** Fills the form with the saved draft and puts it back on the step it was left on. */
    private fun loadDraft() {
        viewModelScope.launch {
            val draft = draftRepo.draft().first() ?: return@launch
            _state.update { it.withDraft(draft) }
        }
    }

    /** Fills the form with the planned visit stored under [visitId]; it stays a planned visit. */
    private fun loadPlannedVisit(visitId: String) {
        viewModelScope.launch {
            val visit =
                visitRepo.get(visitId)?.toUi()?.copy(type = VisitType.Planned) ?: return@launch
            _state.update { it.copy(visit = visit, initialVisit = visit) }
        }
    }

    fun onEvent(event: PlanVisitEvents) {
        when (event) {
            PlanVisitEvents.Back ->
                if (_state.value.asksToSave) _state.update { it.copy(overlay = PlanVisitOverlay.SaveDraft) }
                else sendEvent(UiEvent.NavigationUp)

            PlanVisitEvents.SaveDraft -> saveDraft()
            PlanVisitEvents.DiscardDraft -> discardDraftAndLeave()
            PlanVisitEvents.Next -> moveStep(forward = true)
            PlanVisitEvents.Previous -> moveStep(forward = false)
            is PlanVisitEvents.SelectCity -> updateVisit { it.copy(city = event.value, region = if (it.city == event.value) it.region else null) }
            is PlanVisitEvents.SelectDistrict -> updateVisit { it.copy(region = event.value) }
            is PlanVisitEvents.SelectEntityType -> updateVisit { it.copy(account = event.value) }
            is PlanVisitEvents.SelectPlace -> selectAccount(event.value)
            is PlanVisitEvents.SelectSupervisor -> updateVisit { it.copy(supervisor = event.value) }
            is PlanVisitEvents.SelectDoctors -> updateVisit { it.copy(doctors = event.doctors.distinctBy { doctor -> doctor.id }) }
            is PlanVisitEvents.RemoveDoctor -> if (_state.value.showDoctors) updateVisit { visit -> visit.copy(doctors = visit.doctors.filter { it.id != event.id }) }
            is PlanVisitEvents.SelectShift -> updateVisit { it.copy(shift = event.shift) }
            is PlanVisitEvents.SelectDoubleVisit -> updateVisit { it.copy(isDoubleVisit = event.isDouble) }
            is PlanVisitEvents.OpenAddItem -> _state.update { it.copy(overlay = AddItem(event.kind)) }
            PlanVisitEvents.DismissOverlay -> _state.update { it.copy(overlay = PlanVisitOverlay.None) }
            is PlanVisitEvents.ConfirmAddItems -> confirmAddItems(event.items)
            is PlanVisitEvents.ChangeQuantity -> changeQuantity(
                event.kind,
                event.id,
                event.delta
            )

            PlanVisitEvents.EndVisit -> endVisit()

            PlanVisitEvents.VoiceNoteRecordingStarted -> _state.update { it.copy(isRecordingVoice = true) }
            is PlanVisitEvents.VoiceNoteRecorded -> {
                _state.update { it.copy(isRecordingVoice = false) }
                event.bytes?.takeIf { it.isNotEmpty() }?.let { bytes ->
                    updateVisit { it.copy(voiceNote = bytes) }
                }
            }

            PlanVisitEvents.VoiceNoteDeleted -> updateVisit { it.copy(voiceNote = null) }

            PlanVisitEvents.VoiceNotePermissionDenied ->
                sendEvent(ShowSnackBar(StringResource(R.string.unplanned_voice_permission_denied)))

            PlanVisitEvents.ViewLocationClick -> openVisitLocation()
            PlanVisitEvents.ViewAccountDirectionsClick -> openAccountDirections()
            PlanVisitEvents.ViewAccountLocationClick -> openAccountLocation()

            is PlanVisitEvents.AttachImageResult -> updateVisit {
                if (it.imageNotes.size >= state.value.requiredImages) it else it.copy(imageNotes = it.imageNotes + event.document.uri)
            }
            is PlanVisitEvents.RemoveImage -> updateVisit { it.copy(imageNotes = it.imageNotes.filterIndexed { index, _ -> index != event.index }) }
        }
    }

    /**
     * Stores the finished visit (a planned one replaces its own row, an unplanned one is new), then clears the draft.
     * When storing fails the draft stays, so nothing the rep entered is lost.
     */
    private fun endVisit() {
        val state = _state.value
        if (!state.canEndVisit || state.isSaving) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val visit = state.visit
            try {
                visitRepo.save(
                    visit.toCompletedVisit(
                        stored = visitRepo.get(visit.id),
                        repId = session.user.value.id?.toString(),
                        supervisorId = state.chosenSupervisorId,
                        startedAtMillis = state.openedAtMillis,
                        completedAtMillis = System.currentTimeMillis(),
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                sendEvent(ShowSnackBar(StringResource(R.string.unplanned_visit_save_failed)))
                return@launch
            }
            draftRepo.discard()
            sendEvent(ShowSnackBar(StringResource(R.string.unplanned_visit_ended)))
            sendEvent(UiEvent.NavigationUp)
        }
    }

    private fun saveDraft() {
        val state = _state.value
        _state.update { it.copy(overlay = PlanVisitOverlay.None) }
        viewModelScope.launch {
            draftRepo.save(
                DraftVisit(
                    step = state.currentStep.ordinal,
                    visit = state.visit.toDomain()
                )
            )
            sendEvent(ShowSnackBar(StringResource(R.string.draft_saved)))
            sendEvent(UiEvent.NavigationUp)
        }
    }

    private fun discardDraftAndLeave() {
        _state.update { it.copy(overlay = PlanVisitOverlay.None) }
        viewModelScope.launch {
            draftRepo.discard()
            sendEvent(UiEvent.NavigationUp)
        }
    }

    /** Choosing a place also fills what belongs to its account: specialty, class, address and where it is. */
    private fun selectAccount(data: BasicModelUi) {
        val account = accounts.firstOrNull { it.id == data.id } ?: return
        updateVisit {
            it.copy(
                doctors = if (it.accountId == account.id) it.doctors else emptyList(),
                workingTime = account.workingTime,
                shift = account.workingTime.onlyShift ?: it.shift,
                placeName = account.name,
                accountId = account.id,
                specialty = account.specialty?.toUi(),
                classCategory = account.classCategory?.toUi(),
                address = account.address,
                accountLatitude = account.latitude,
                accountLongitude = account.longitude,
            )
        }
    }

    /** Forgets the chosen place and everything that came from its account. */
    private fun VisitUi.withoutPlace() = copy(
        placeName = "",
        accountId = "",
        doctors = emptyList(),
        workingTime = WorkingTime.Both,
        specialty = null,
        classCategory = null,
        address = "",
        accountLatitude = 0.0,
        accountLongitude = 0.0,
    )

    private fun openVisitLocation() {
        val visit = _state.value.visit
        if (visit.hasLocations) {
            sendEvent(
                Navigate(
                    MainGraph.VisitLocationMap(
                        visitLatitude = visit.visitLatitude,
                        visitLongitude = visit.visitLongitude,
                        accountLatitude = visit.accountLatitude,
                        accountLongitude = visit.accountLongitude,
                        travelSeconds = visit.travelSeconds,
                    )
                )
            )
        } else {
            sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
        }
    }

    private fun openAccountLocation() {
        val visit = _state.value.visit
        if (visit.hasAccountLocation) {
            sendEvent(
                Navigate(
                    MainGraph.AccountLocation(
                        accountLatitude = visit.accountLatitude,
                        accountLongitude = visit.accountLongitude,
                        accountName = visit.placeName,
                        address = visit.accountAddressLine(),
                        classCategory = visit.classCategory?.name.orEmpty(),
                        accountType = visit.account?.name.orEmpty(),
                        specialty = visit.specialty?.name.orEmpty(),
                    )
                )
            )
        } else {
            sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
        }
    }

    private fun openAccountDirections() {
        val visit = _state.value.visit
        if (visit.hasAccountLocation) {
            sendEvent(
                Navigate(
                    MainGraph.AccountDirections(
                        visit.accountLatitude,
                        visit.accountLongitude,
                        visit.placeName
                    )
                )
            )
        } else {
            sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
        }
    }

    private fun moveStep(forward: Boolean) {
        if (forward && !_state.value.canGoNext) return
        val steps = PlanVisitStep.entries
        _state.update { state ->
            val index =
                (state.currentStep.ordinal + if (forward) 1 else -1).coerceIn(0, steps.lastIndex)
            state.copy(currentStep = steps[index])
        }
        val current = _state.value
        if (current.currentStep == PlanVisitStep.Result && current.capturedAtMillis == null) captureLocation()
    }

    /** Stores where the rep was last seen by the session (and its saved street) as the visit location. */
    private fun captureLocation() {
        val user = session.user.value
        val location = user.lastLocation
        if (location == null) {
            sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
            return
        }
        _state.update {
            it.copy(
                visit = it.visit.copy(
                    visitLatitude = location.latitude,
                    visitLongitude = location.longitude
                ),
                capturedAtMillis = user.lastLocationTimeMillis,
                locationAddress = user.lastAddress,
                locationAccuracyMeters = user.lastAccuracyMeters,
            )
        }
    }

    private fun updateVisit(transform: (VisitUi) -> VisitUi) {
        _state.update { it.copy(visit = transform(it.visit)) }
    }

    /** Adding an item that is already on the visit raises its quantity instead of duplicating the row. */
    private fun confirmAddItems(items: List<ProductUi>) {
        val overlay = _state.value.overlay as? PlanVisitOverlay.AddItem ?: return
        if (items.isEmpty()) return
        _state.update { state ->
            val merged = items.fold(state.visit.itemsOf(overlay.kind)) { lines, added ->
                val existing = lines.find { it.id == added.id }
                if (existing == null) {
                    lines + added
                } else {
                    lines.map { if (it.id == added.id) it.copy(quantity = it.quantity + added.quantity) else it }
                }
            }
            state.copy(
                visit = state.visit.withItems(overlay.kind, merged),
                overlay = PlanVisitOverlay.None,
            )
        }
    }

    /** Quantity can't exceed what is left in custody; reaching 0 removes the row. */
    private fun changeQuantity(kind: ProductType, id: String, delta: Int) {
        _state.update { state ->
            val lines = state.visit.itemsOf(kind).mapNotNull { item ->
                if (item.id != id) return@mapNotNull item
                val quantity = (item.quantity + delta).coerceAtMost(item.remaining)
                if (quantity <= 0) null else item.copy(quantity = quantity)
            }
            state.copy(visit = state.visit.withItems(kind, lines))
        }
    }

    private fun VisitUi.itemsOf(kind: ProductType): List<ProductUi> = when (kind) {
        ProductType.Gift -> gifts
        else -> products
    }

    private fun VisitUi.withItems(kind: ProductType, items: List<ProductUi>): VisitUi =
        when (kind) {
            ProductType.Gift -> copy(gifts = items)
            else -> copy(products = items)
        }
}
