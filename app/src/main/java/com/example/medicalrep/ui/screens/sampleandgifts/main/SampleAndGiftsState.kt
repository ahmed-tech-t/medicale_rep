package com.example.medicalrep.ui.screens.sampleandgifts.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ProductType

/** Samples are the custody of kind Product, gifts the custody of kind Gift. */
enum class SampleAndGiftsTab(val kind: ProductType) {
    Samples(ProductType.Sample),
    Gifts(ProductType.Gift),
}

/** The list is paged and comes from the ViewModel's `items` flow. */
@Immutable
data class SampleAndGiftsState(
    val selectedTab: SampleAndGiftsTab = SampleAndGiftsTab.Samples,
)
