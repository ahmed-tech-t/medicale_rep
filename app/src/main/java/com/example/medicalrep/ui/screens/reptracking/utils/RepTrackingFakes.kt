package com.example.medicalrep.ui.screens.reptracking.utils

import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.RepUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.screens.reptracking.main.RepTrackingState

/** Sample data shown until the rep's visits are loaded. */
object RepTrackingFakes {

    private fun item(id: String, kind: ProductType, quantity: Int) =
        ProductUi(id = id, name = id, quantity = quantity, kind = kind, remaining = 0, description = "")

    fun visits(): List<VisitUi> = listOf(
        VisitSamples.planned(id = "rep-1", placeName = "د. هاني المسيري", account = "عيادة باطنة", specialty = "")
            .toUi()
            .copy(
                time = "09:30 ص",
                products = listOf(item("p1", ProductType.Product, 10)),
                gifts = listOf(item("g1", ProductType.Gift, 2)),
            ),
        VisitSamples.planned(
            id = "rep-2",
            placeName = "د. محمود عبد الرحمن",
            account = "عيادة",
            specialty = "استشاري باطنة",
        ).toUi().copy(time = "10:45 ص", products = listOf(item("p2", ProductType.Product, 8))),
    )

    fun state(): RepTrackingState = RepTrackingState(
        reps = listOf(RepUi("rep-1", "أحمد محمد"), RepUi("rep-2", "سارة علي")),
        selectedRepId = "rep-1",
        dateMillis = 1_791_072_000_000L,
        visits = visits(),
        progress = VisitProgress(completed = 2, planned = 5, missed = 1),
    )
}
