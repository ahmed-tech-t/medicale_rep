package com.example.medicalrep.ui.screens.planvisit.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.ui.common.location.WEAK_GPS_METERS
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import java.util.Collections.emptyList

enum class PlanVisitStep { Entity, GiftsProducts, Result }

sealed interface PlanVisitOverlay {
    data object None : PlanVisitOverlay
    data class AddItem(val kind: ProductType) : PlanVisitOverlay
    data object SaveDraft : PlanVisitOverlay
}

/** The form filled with a saved draft, on the step it was left on. */
fun PlanVisitState.withDraft(draft: DraftVisit): PlanVisitState {
    val visit = draft.visit.toUi()
    return copy(
        visit = visit,
        initialVisit = visit,
        currentStep = PlanVisitStep.entries[draft.step.coerceIn(0, PlanVisitStep.entries.lastIndex)],
    )
}

@Immutable
data class PlanVisitState(
    val currentStep: PlanVisitStep = PlanVisitStep.Entity,
    val visit: VisitUi = VisitUi(),
    /** The visit as it was when the screen opened (or the saved draft was loaded): used to tell if anything changed. */
    val initialVisit: VisitUi = VisitUi(),
    val overlay: PlanVisitOverlay = PlanVisitOverlay.None,
    val isRecordingVoice: Boolean = false,
    /** The visit is being stored; a second tap on End visit is ignored. */
    val isSaving: Boolean = false,
    val capturedAtMillis: Long? = null,
    val locationAddress: String? = null,
    /** How many metres the captured location may be off; null when it is not known. */
    val locationAccuracyMeters: Float? = null,
    val giftCatalog: List<ProductUi> = emptyList(),
    val productCatalog: List<ProductUi> = emptyList(),
    val cityOptions: List<String> = emptyList(),
    val districtOptions: List<String> = emptyList(),
    val entityTypeOptions: List<String> = emptyList(),
    val placeOptions: List<String> = emptyList(),
    /** The supervisors the form can choose from; a double visit stores the id of the chosen one. */
    val supervisors: List<User> = emptyList(),
    /** When the form opened: the visit started then. */
    val openedAtMillis: Long = 0L,
) {
    val supervisorOptions: List<String> get() = supervisors.map(User::displayName)

    /** The id of the supervisor chosen on the form; null when none is chosen or the visit is single. */
    val chosenSupervisorId: String?
        get() = if (visit.isDoubleVisit) supervisors.firstOrNull { it.displayName == visit.supervisorName }?.id else null

    /** The GPS reading behind the captured location is too rough to trust (worse than [WEAK_GPS_METERS]). */
    val hasWeakSignal: Boolean get() = (locationAccuracyMeters ?: 0f) > WEAK_GPS_METERS

    /** Back asks to save only while a visit in progress was changed; the finished result step just leaves. */
    val asksToSave: Boolean get() = visit != initialVisit && currentStep != PlanVisitStep.Result
    /** Step 1 is done: city, district, type and place are chosen (the specialty comes from the place); the supervisor only matters for a double visit. */
    val isEntityComplete: Boolean
        get() = with(visit) {
            city.isNotBlank() && region.isNotBlank() && account.isNotBlank() && placeName.isNotBlank() &&
                (!isDoubleVisit || supervisorName.isNotBlank())
        }

    /** Step 1 must be complete; step 2 (gifts and products) is optional. */
    val canGoNext: Boolean
        get() = when (currentStep) {
            PlanVisitStep.Entity -> isEntityComplete
            PlanVisitStep.GiftsProducts -> true
            PlanVisitStep.Result -> false
        }

    /** The visit is proven by a recorded voice note and an attached image. */
    val canEndVisit: Boolean get() = visit.voiceNote != null && visit.imageNote != null
    val giftsCount: Int get() = visit.gifts.sumOf { it.quantity }
    val productsCount: Int get() = visit.products.sumOf { it.quantity }
}
