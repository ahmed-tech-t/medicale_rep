package com.example.medicalrep.ui.screens.visitdetails.utils

import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.screens.visitdetails.main.VisitDetailsState

/** Sample data shown until the visit is loaded from Room. */
object VisitDetailsFakes {

    fun visit(id: String): VisitUi = VisitSamples.planned(
        id = id,
        placeName = "صيدليه الايمان/هليوبلس",
        account = "Pharmacy (صيدلية)",
    ).toUi().copy(
        region = BasicModelUi("Heliopolis", "Heliopolis"),
        city = BasicModelUi("All Bricks", "All Bricks"),
        date = "2026-10-20",
        time = "18:37:25",
        visitLatitude = 30.560491,
        visitLongitude = 31.018593,
        accountLatitude = 30.547956,
        accountLongitude = 31.133557,
        travelSeconds = 25 * 60,
    )

    fun state(id: String = "visit-1"): VisitDetailsState = VisitDetailsState(visit = visit(id))
}
