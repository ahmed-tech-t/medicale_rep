package com.example.medicalrep.ui.screens.unplannedvisit.main

import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.models.ProductUi

sealed interface UnplannedVisitEvents {
    data object Back : UnplannedVisitEvents
    data object Next : UnplannedVisitEvents
    data object Previous : UnplannedVisitEvents

    data class SelectCity(val value: String) : UnplannedVisitEvents
    data class SelectDistrict(val value: String) : UnplannedVisitEvents
    data class SelectEntityType(val value: String) : UnplannedVisitEvents
    data class SelectPlace(val value: String) : UnplannedVisitEvents
    data class SelectSpecialty(val value: String) : UnplannedVisitEvents
    data class SelectSupervisor(val value: String) : UnplannedVisitEvents
    data class SelectShift(val shift: Shift) : UnplannedVisitEvents
    data class SelectDoubleVisit(val isDouble: Boolean) : UnplannedVisitEvents

    data class OpenAddItem(val kind: ProductType) : UnplannedVisitEvents
    data object DismissOverlay : UnplannedVisitEvents
    data class ConfirmAddItems(val items: List<ProductUi>) : UnplannedVisitEvents
    data class ChangeQuantity(val kind: ProductType, val id: String, val delta: Int) : UnplannedVisitEvents

    data class ToggleClinicOpen(val confirmed: Boolean) : UnplannedVisitEvents
    data object EndVisit : UnplannedVisitEvents
    data object VoiceNoteClick : UnplannedVisitEvents
    data object AttachClick : UnplannedVisitEvents
    data object ViewLocationClick : UnplannedVisitEvents
}
