package com.example.medicalrep.ui.screens.unplannedvisit.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import java.util.Collections.emptyList

enum class UnplannedVisitStep { Entity, GiftsProducts, Result }

sealed interface UnplannedVisitOverlay {
    data object None : UnplannedVisitOverlay
    data class AddItem(val kind: ProductType) : UnplannedVisitOverlay
}

@Immutable
data class UnplannedVisitState(
    val currentStep: UnplannedVisitStep = UnplannedVisitStep.Entity,
    val visit: VisitUi = VisitUi(),
    val overlay: UnplannedVisitOverlay = UnplannedVisitOverlay.None,
    val giftCatalog: List<ProductUi> = emptyList(),
    val productCatalog: List<ProductUi> = emptyList(),
    val cityOptions: List<String> = emptyList(),
    val districtOptions: List<String> = emptyList(),
    val entityTypeOptions: List<String> = emptyList(),
    val placeOptions: List<String> = emptyList(),
    val specialtyOptions: List<String> = emptyList(),
    val supervisorOptions: List<String> = emptyList(),
    val clinicOpenConfirmed: Boolean = false,
) {
    val giftsCount: Int get() = visit.gifts.sumOf { it.quantity }
    val productsCount: Int get() = visit.products.sumOf { it.quantity }
}
