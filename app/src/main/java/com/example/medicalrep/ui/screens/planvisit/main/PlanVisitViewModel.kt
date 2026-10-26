package com.example.medicalrep.ui.screens.planvisit.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiEvent.*
import com.appcore.core.domain.utils.UiText
import com.appcore.core.domain.utils.UiText.*
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.GeocodingRepo
import com.example.medicalrep.ui.common.location.LocationHelper
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitOverlay.*
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.screens.planvisit.utils.accountAddressLine
import com.example.medicalrep.ui.screens.visitdetails.utils.hasAccountLocation
import com.example.medicalrep.ui.screens.visitdetails.utils.hasVisitLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanVisitViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val locationHelper: LocationHelper,
    private val geocodingRepo: GeocodingRepo,
) : BaseViewModel(appSettingsRepo) {

    /** Null means an unplanned visit: every field stays editable. */
    private val visitId: String? = savedStateHandle.toRoute<MainGraph.PlanVisit>().visitId
    private val isNewVisit: Boolean = savedStateHandle.toRoute<MainGraph.PlanVisit>().isNew

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<PlanVisitState> = _state.asStateFlow()

    init {
        visitId?.let(::loadPlannedVisit)
    }

    private fun initialState(): PlanVisitState {
        val sample = PlanVisitFakes.state()
        // Until planned visits load from Room, a planned visit shows the sample visit's data.
        return if (visitId == null) sample
        else sample.copy(visit = sample.visit.copy(id = visitId, type = VisitType.Planned))
    }

    // TODO: load the planned visit from Room by [visitId] and assign it to `visit` (keep type = Planned).
    private fun loadPlannedVisit(visitId: String) = Unit

    fun onEvent(event: PlanVisitEvents) {
        when (event) {
            PlanVisitEvents.Back -> sendEvent(UiEvent.NavigationUp)
            PlanVisitEvents.Next -> moveStep(forward = true)
            PlanVisitEvents.Previous -> moveStep(forward = false)
            is PlanVisitEvents.SelectCity -> updateVisit { it.copy(city = event.value) }
            is PlanVisitEvents.SelectDistrict -> updateVisit { it.copy(region = event.value) }
            is PlanVisitEvents.SelectEntityType -> updateVisit { it.copy(account = event.value) }
            is PlanVisitEvents.SelectPlace -> updateVisit { it.copy(placeName = event.value) }
            is PlanVisitEvents.SelectSpecialty -> updateVisit { it.copy(specialty = event.value) }
            is PlanVisitEvents.SelectSupervisor -> updateVisit { it.copy(supervisorName = event.value) }
            is PlanVisitEvents.SelectShift -> updateVisit { it.copy(shift = event.shift) }
            is PlanVisitEvents.SelectDoubleVisit -> updateVisit { it.copy(isDoubleVisit = event.isDouble) }
            is PlanVisitEvents.OpenAddItem ->
                _state.update { it.copy(overlay = AddItem(event.kind)) }

            PlanVisitEvents.DismissOverlay -> _state.update { it.copy(overlay = PlanVisitOverlay.None) }
            is PlanVisitEvents.ConfirmAddItems -> confirmAddItems(event.items)
            is PlanVisitEvents.ChangeQuantity -> changeQuantity(
                event.kind,
                event.id,
                event.delta
            )

            is PlanVisitEvents.ToggleClinicOpen -> updateVisit { it.copy(noIssueFound = event.confirmed) }
            PlanVisitEvents.EndVisit -> {
                sendEvent(ShowSnackBar(StringResource(R.string.unplanned_visit_ended)))
                sendEvent(UiEvent.NavigationUp)
            }

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
            PlanVisitEvents.RefreshLocation -> captureLocation()

            is PlanVisitEvents.AttachImageResult -> updateVisit { it.copy(imageNote = event.document.uri) }
        }
    }

    private fun openVisitLocation() {
        val visit = _state.value.visit
        if (visit.hasVisitLocation) {
            sendEvent(Navigate(MainGraph.VisitLocationMap(visit.visitLatitude, visit.visitLongitude)))
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
                        classCategory = visit.classCategory,
                        accountType = visit.account,
                        specialty = visit.specialty,
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
            sendEvent(Navigate(MainGraph.AccountDirections(visit.accountLatitude, visit.accountLongitude, visit.placeName)))
        } else {
            sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
        }
    }

    private fun moveStep(forward: Boolean) {
        val steps = PlanVisitStep.entries
        _state.update { state ->
            val index =
                (state.currentStep.ordinal + if (forward) 1 else -1).coerceIn(0, steps.lastIndex)
            state.copy(currentStep = steps[index])
        }
        val current = _state.value
        if (current.currentStep == PlanVisitStep.Result && current.capturedAtMillis == null) captureLocation()
    }

    /** Stores where the rep is now (and its address, when the lookup works) as the visit location. */
    private fun captureLocation() {
        if (_state.value.isLocating) return
        _state.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            val location = locationHelper.getLocation()
            if (location == null) {
                _state.update { it.copy(isLocating = false) }
                sendEvent(ShowSnackBar(StringResource(R.string.visit_location_unavailable)))
                return@launch
            }
            _state.update {
                it.copy(
                    visit = it.visit.copy(visitLatitude = location.latitude, visitLongitude = location.longitude),
                    capturedAtMillis = System.currentTimeMillis(),
                    locationAddress = null,
                    isLocating = false,
                )
            }
            geocodingRepo.getAddress(RoutePoint(location.latitude, location.longitude)).collect { address ->
                if (address is Resource.Success) _state.update { it.copy(locationAddress = address.data) }
            }
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
        ProductType.Product -> products
    }

    private fun VisitUi.withItems(kind: ProductType, items: List<ProductUi>): VisitUi =
        when (kind) {
            ProductType.Gift -> copy(gifts = items)
            ProductType.Product -> copy(products = items)
        }
}
