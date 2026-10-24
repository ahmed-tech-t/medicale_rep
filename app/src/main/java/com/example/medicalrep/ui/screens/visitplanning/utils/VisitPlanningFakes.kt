package com.example.medicalrep.ui.screens.visitplanning.utils

import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState

/** Sample data shown until visits are loaded from Room. */
object VisitPlanningFakes {

    fun pendingVisits(): List<VisitUi> = List(5) { index ->
        VisitSamples.planned(
            id = "pending-$index",
            placeName = "د. هاني عبد الفتاح (عظام)",
            account = "عيادة",
            specialty = "استشاري باطنة وجهاز هضمي",
        ).toUi().copy(type = VisitType.Planning, date = "أمس", time = "04:15 م")
    }

    fun visits(): List<VisitUi> = List(4) { index ->
        VisitSamples.planned(
            id = "visit-$index",
            placeName = "د. محمد عبد الرحمن الشافعي",
            account = "عيادة",
            specialty = "استشاري باطنة وجهاز هضمي",
        ).toUi()
    }

    fun state(): VisitPlanningState = VisitPlanningState(
        pendingVisits = pendingVisits(),
        visits = visits(),
        scheduleDateMillis = 1_792_000_000_000L,
    )
}
