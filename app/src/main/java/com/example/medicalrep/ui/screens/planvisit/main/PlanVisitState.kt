package com.example.medicalrep.ui.screens.planvisit.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import java.util.Collections.emptyList

enum class PlanVisitStep { Entity, GiftsProducts, Result }

sealed interface PlanVisitOverlay {
    data object None : PlanVisitOverlay
    data class AddItem(val kind: ProductType) : PlanVisitOverlay
}

@Immutable
data class PlanVisitState(
    val currentStep: PlanVisitStep = PlanVisitStep.Entity,
    val visit: VisitUi = VisitUi(),
    val overlay: PlanVisitOverlay = PlanVisitOverlay.None,
    val isRecordingVoice: Boolean = false,
    val giftCatalog: List<ProductUi> = emptyList(),
    val productCatalog: List<ProductUi> = emptyList(),
    val cityOptions: List<String> = emptyList(),
    val districtOptions: List<String> = emptyList(),
    val entityTypeOptions: List<String> = emptyList(),
    val placeOptions: List<String> = emptyList(),
    val specialtyOptions: List<String> = emptyList(),
    val supervisorOptions: List<String> = emptyList(),
) {
    val giftsCount: Int get() = visit.gifts.sumOf { it.quantity }
    val productsCount: Int get() = visit.products.sumOf { it.quantity }
}
