package com.example.medicalrep.ui.screens.planvisit.main

import com.appcore.core.ui.utils.document_picker.PickedDocument
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.models.ProductUi

sealed interface PlanVisitEvents {
    data object Back : PlanVisitEvents
    data object SaveDraft : PlanVisitEvents
    data object DiscardDraft : PlanVisitEvents
    data object Next : PlanVisitEvents
    data object Previous : PlanVisitEvents

    data class SelectCity(val value: String) : PlanVisitEvents
    data class SelectDistrict(val value: String) : PlanVisitEvents
    data class SelectEntityType(val value: String) : PlanVisitEvents
    data class SelectPlace(val value: String) : PlanVisitEvents
    data class SelectSpecialty(val value: String) : PlanVisitEvents
    data class SelectSupervisor(val value: String) : PlanVisitEvents
    data class SelectShift(val shift: Shift) : PlanVisitEvents
    data class SelectDoubleVisit(val isDouble: Boolean) : PlanVisitEvents

    data class OpenAddItem(val kind: ProductType) : PlanVisitEvents
    data object DismissOverlay : PlanVisitEvents
    data class ConfirmAddItems(val items: List<ProductUi>) : PlanVisitEvents
    data class ChangeQuantity(val kind: ProductType, val id: String, val delta: Int) : PlanVisitEvents
    data class AttachImageResult(val document: PickedDocument) : PlanVisitEvents
    data class ToggleClinicOpen(val confirmed: Boolean) : PlanVisitEvents
    data object EndVisit : PlanVisitEvents
    data object VoiceNoteRecordingStarted : PlanVisitEvents

    /** [bytes] is null or empty when the clip was too short to keep. */
    data class VoiceNoteRecorded(val bytes: ByteArray?) : PlanVisitEvents
    data object VoiceNotePermissionDenied : PlanVisitEvents
    data object VoiceNoteDeleted : PlanVisitEvents
    data object ViewLocationClick : PlanVisitEvents
    data object ViewAccountDirectionsClick : PlanVisitEvents
    data object ViewAccountLocationClick : PlanVisitEvents
    data object RefreshLocation : PlanVisitEvents
}
