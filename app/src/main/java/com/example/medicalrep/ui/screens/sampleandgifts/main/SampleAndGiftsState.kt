package com.example.medicalrep.ui.screens.sampleandgifts.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.screens.sampleandgifts.models.SampleAndGiftsItemType
import com.example.medicalrep.ui.screens.sampleandgifts.models.SampleAndGiftsItemUi

enum class SampleAndGiftsTab(val itemType: SampleAndGiftsItemType) {
    Samples(SampleAndGiftsItemType.Sample),
    Gifts(SampleAndGiftsItemType.Gift),
}

@Immutable
data class SampleAndGiftsState(
    val selectedTab: SampleAndGiftsTab = SampleAndGiftsTab.Samples,
    val items: List<SampleAndGiftsItemUi> = emptyList(),
) {
    /** Items of the selected tab, in source order. */
    val visibleItems: List<SampleAndGiftsItemUi>
        get() = items.filter { it.type == selectedTab.itemType }
}
