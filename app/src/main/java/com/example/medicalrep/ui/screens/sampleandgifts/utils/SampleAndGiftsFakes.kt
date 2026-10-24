package com.example.medicalrep.ui.screens.sampleandgifts.utils

import com.example.medicalrep.ui.screens.sampleandgifts.main.SampleAndGiftsState
import com.example.medicalrep.ui.screens.sampleandgifts.models.SampleAndGiftsItemType
import com.example.medicalrep.ui.screens.sampleandgifts.models.SampleAndGiftsItemUi

object SampleAndGiftsFakes {
    val items: List<SampleAndGiftsItemUi> by lazy {
        listOf(
            SampleAndGiftsItemUi(
                id = "sample-1",
                type = SampleAndGiftsItemType.Sample,
                name = "أوميبرازول فورت 40 مجم",
                received = 100,
                dispatched = 62,
                remaining = 38,
            ),
            SampleAndGiftsItemUi(
                id = "sample-2",
                type = SampleAndGiftsItemType.Sample,
                name = "شراب برونكوكير للأطفال 120 مل",
                received = 80,
                dispatched = 40,
                remaining = 40,
            ),
            SampleAndGiftsItemUi(
                id = "sample-3",
                type = SampleAndGiftsItemType.Sample,
                name = "كاردكس بلس 10 مجم أقراص",
                received = 70,
                dispatched = 63,
                remaining = 7,
            ),
            SampleAndGiftsItemUi(
                id = "sample-4",
                type = SampleAndGiftsItemType.Sample,
                name = "أموكسيسيلين 500 مجم",
                received = 60,
                dispatched = 35,
                remaining = 25,
            ),
            SampleAndGiftsItemUi(
                id = "sample-5",
                type = SampleAndGiftsItemType.Sample,
                name = "بانادول إكسترا",
                received = 90,
                dispatched = 50,
                remaining = 40,
            ),
            SampleAndGiftsItemUi(
                id = "sample-6",
                type = SampleAndGiftsItemType.Sample,
                name = "كونكور 5 مجم",
                received = 45,
                dispatched = 20,
                remaining = 25,
            ),
            SampleAndGiftsItemUi(
                id = "gift-1",
                type = SampleAndGiftsItemType.Gift,
                name = "قلم معدن فاخر محفور باسم الشركة",
                received = 100,
                dispatched = 62,
                remaining = 15,
            ),
            SampleAndGiftsItemUi(
                id = "gift-2",
                type = SampleAndGiftsItemType.Gift,
                name = "دفتر ملاحظات جلد فاخر",
                received = 50,
                dispatched = 28,
                remaining = 22,
            ),
            SampleAndGiftsItemUi(
                id = "gift-3",
                type = SampleAndGiftsItemType.Gift,
                name = "حقيبة طبية عملية",
                received = 20,
                dispatched = 12,
                remaining = 8,
            ),
            SampleAndGiftsItemUi(
                id = "gift-4",
                type = SampleAndGiftsItemType.Gift,
                name = "كوب حراري بشعار الشركة",
                received = 40,
                dispatched = 18,
                remaining = 22,
            ),
        )
    }

    fun state(): SampleAndGiftsState = SampleAndGiftsState(items = items)
}
