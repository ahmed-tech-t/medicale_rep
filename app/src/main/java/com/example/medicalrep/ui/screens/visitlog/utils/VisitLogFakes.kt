package com.example.medicalrep.ui.screens.visitlog.utils

import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogState

/** Sample data shown until visits are loaded from Room. */
object VisitLogFakes {

    fun requiredVisits(): List<VisitUi> = List(4) { index ->
        VisitSamples.planned(
            id = "required-$index",
            placeName = "د. طارق عز الدين",
            specialty = "باطنة",
        ).toUi().copy(
            address = "شارع الجلاء - برج الأطباء",
            shift = if (index % 2 == 0) Shift.Am else Shift.Pm,
            classCategory = listOf("A", "B", "C")[index % 3].let { BasicModelUi(it, it) },
        )
    }

    fun doneVisits(): List<VisitUi> = listOf(
        VisitSamples.planned(id = "done-1", placeName = "د. محمد عبد الرحمن الشافعي", account = "عيادة")
            .toUi()
            .copy(isDoubleVisit = true, supervisor = BasicModelUi("sup-1", "احمد حسن")),
        VisitSamples.unplanned(id = "done-2", placeName = "د. منى الشاذلي (أطفال)")
            .toUi()
            .copy(
                backendId = "432423",
                syncStatus = SyncStatus.Synced,
                isDoubleVisit = true,
                supervisor = BasicModelUi("sup-1", "احمد حسن"),
                address = "زيارة إضافية في نفس المربع الجغرافي",
            ),
    )

    fun state(): VisitLogState = VisitLogState(resultCount = requiredVisits().size)
}
