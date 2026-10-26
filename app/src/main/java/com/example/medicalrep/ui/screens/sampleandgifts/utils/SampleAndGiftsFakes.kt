package com.example.medicalrep.ui.screens.sampleandgifts.utils

import com.example.medicalrep.ui.models.CustodyUi

/** Sample data for previews. */
object SampleAndGiftsFakes {

    private fun item(id: String, name: String, isGift: Boolean, received: Int, dispatched: Int) = CustodyUi(
        id = id,
        name = name,
        isGift = isGift,
        received = received,
        dispatched = dispatched,
        remaining = received - dispatched,
    )

    val samples: List<CustodyUi> = listOf(
        item("sample-1", "أوميبرازول فورت 40 مجم", false, 100, 62),
        item("sample-2", "شراب برونكوكير للأطفال 120 مل", false, 80, 40),
        item("sample-3", "كاردكس بلس 10 مجم أقراص", false, 70, 63),
        item("sample-4", "أموكسيسيلين 500 مجم", false, 60, 35),
        item("sample-5", "بانادول إكسترا", false, 90, 50),
        item("sample-6", "كونكور 5 مجم", false, 45, 20),
    )

    val gifts: List<CustodyUi> = listOf(
        item("gift-1", "قلم معدن فاخر محفور باسم الشركة", true, 100, 85),
        item("gift-2", "دفتر ملاحظات جلد فاخر", true, 50, 28),
        item("gift-3", "حقيبة طبية عملية", true, 20, 12),
        item("gift-4", "كوب حراري بشعار الشركة", true, 40, 18),
    )
}
