package com.example.medicalrep.ui.models

import com.example.medicalrep.R

/** Temporary fake custody items until API is wired. */
object StaticsGiftsFakes {
    val items: List<StaticsGiftsItemUi> by lazy {
        listOf(
            StaticsGiftsItemUi(
                id = "samples",
                titleRes = R.string.statics_custody_samples_title,
                receivedFormatRes = R.string.statics_custody_samples_received_format,
                receivedQuantity = 250,
                skuCount = 6,
                remaining = 85,
                dispatched = 165,
            ),
            StaticsGiftsItemUi(
                id = "gifts",
                titleRes = R.string.statics_custody_gifts_title,
                receivedFormatRes = R.string.statics_custody_gifts_received_format,
                receivedQuantity = 90,
                skuCount = 4,
                remaining = 28,
                dispatched = 62,
            ),
        )
    }
}
