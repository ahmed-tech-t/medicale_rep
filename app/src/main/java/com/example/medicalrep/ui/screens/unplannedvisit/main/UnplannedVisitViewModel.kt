package com.example.medicalrep.ui.screens.unplannedvisit.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class UnplannedVisitViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(UnplannedVisitFakes.state())
    val state: StateFlow<UnplannedVisitState> = _state.asStateFlow()

    fun onEvent(event: UnplannedVisitEvents) {
        when (event) {
            UnplannedVisitEvents.Back -> sendEvent(UiEvent.NavigationUp)
            UnplannedVisitEvents.Next -> moveStep(forward = true)
            UnplannedVisitEvents.Previous -> moveStep(forward = false)
            is UnplannedVisitEvents.SelectCity -> updateVisit { it.copy(city = event.value) }
            is UnplannedVisitEvents.SelectDistrict -> updateVisit { it.copy(region = event.value) }
            is UnplannedVisitEvents.SelectEntityType -> updateVisit { it.copy(account = event.value) }
            is UnplannedVisitEvents.SelectPlace -> updateVisit { it.copy(placeName = event.value) }
            is UnplannedVisitEvents.SelectSpecialty -> updateVisit { it.copy(specialty = event.value) }
            is UnplannedVisitEvents.SelectSupervisor -> updateVisit { it.copy(supervisorName = event.value) }
            is UnplannedVisitEvents.SelectShift -> updateVisit { it.copy(shift = event.shift) }
            is UnplannedVisitEvents.SelectDoubleVisit -> updateVisit { it.copy(isDoubleVisit = event.isDouble) }
            is UnplannedVisitEvents.OpenAddItem ->
                _state.update { it.copy(overlay = UnplannedVisitOverlay.AddItem(event.kind)) }
            UnplannedVisitEvents.DismissOverlay -> _state.update { it.copy(overlay = UnplannedVisitOverlay.None) }
            is UnplannedVisitEvents.ConfirmAddItems -> confirmAddItems(event.items)
            is UnplannedVisitEvents.ChangeQuantity -> changeQuantity(event.kind, event.id, event.delta)
            is UnplannedVisitEvents.ToggleClinicOpen ->
                _state.update { it.copy(clinicOpenConfirmed = event.confirmed) }
            UnplannedVisitEvents.EndVisit -> {
                sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.unplanned_visit_ended)))
                sendEvent(UiEvent.NavigationUp)
            }
            UnplannedVisitEvents.VoiceNoteClick,
            UnplannedVisitEvents.AttachClick,
            UnplannedVisitEvents.ViewLocationClick ->
                sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.placeholder_coming_soon)))
        }
    }

    private fun moveStep(forward: Boolean) {
        val steps = UnplannedVisitStep.entries
        _state.update { state ->
            val index = (state.currentStep.ordinal + if (forward) 1 else -1).coerceIn(0, steps.lastIndex)
            state.copy(currentStep = steps[index])
        }
    }

    private fun updateVisit(transform: (VisitUi) -> VisitUi) {
        _state.update { it.copy(visit = transform(it.visit)) }
    }

    /** Adding an item that is already on the visit raises its quantity instead of duplicating the row. */
    private fun confirmAddItems(items: List<ProductUi>) {
        val overlay = _state.value.overlay as? UnplannedVisitOverlay.AddItem ?: return
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
                overlay = UnplannedVisitOverlay.None,
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

    private fun VisitUi.withItems(kind: ProductType, items: List<ProductUi>): VisitUi = when (kind) {
        ProductType.Gift -> copy(gifts = items)
        ProductType.Product -> copy(products = items)
    }
}
