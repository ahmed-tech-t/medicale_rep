package com.example.medicalrep.ui.screens.planvisit.main

import com.appcore.core.ui.utils.document_picker.PickedDocument
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.DoctorUi
import com.example.medicalrep.ui.models.ProductUi

sealed interface PlanVisitEvents {
    data object Back : PlanVisitEvents
    data object SaveDraft : PlanVisitEvents
    data object DiscardDraft : PlanVisitEvents
    data object Next : PlanVisitEvents
    data object Previous : PlanVisitEvents

    data class SelectCity(val value: BasicModelUi) : PlanVisitEvents
    data class SelectDistrict(val value: BasicModelUi) : PlanVisitEvents
    data class SelectEntityType(val value: BasicModelUi) : PlanVisitEvents
    data class SelectPlace(val value: BasicModelUi) : PlanVisitEvents
    data class SelectSupervisor(val value: BasicModelUi) : PlanVisitEvents
    /** The doctors the visit is for: replaces the earlier pick. */
    data class SelectDoctors(val doctors: List<DoctorUi>) : PlanVisitEvents
    data class RemoveDoctor(val id: String) : PlanVisitEvents
    data class SelectShift(val shift: Shift) : PlanVisitEvents
    data class SelectDoubleVisit(val isDouble: Boolean) : PlanVisitEvents

    data class OpenAddItem(val kind: ProductType) : PlanVisitEvents
    data object DismissOverlay : PlanVisitEvents
    data class ConfirmAddItems(val items: List<ProductUi>) : PlanVisitEvents
    data class ChangeQuantity(val kind: ProductType, val id: String, val delta: Int) : PlanVisitEvents
    data class AttachImageResult(val document: PickedDocument) : PlanVisitEvents
    data class RemoveImage(val index: Int) : PlanVisitEvents
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
}
