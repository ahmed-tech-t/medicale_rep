package com.example.medicalrep.ui.preview

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import java.time.LocalDate
import java.time.LocalTime

/** Shared sample visits for Previews, catalog, and UI tests. */
object VisitSamples {

    private fun option(name: String) = BasicModel(name, name)

    val sampleDate: LocalDate = LocalDate.of(2026, 10, 19)

    fun planned(
        id: String = "v1",
        placeName: String = "عيادة د. محمود عبد الرحمن",
        account: String = "د. محمود",
        specialty: String = "استشاري باطنة",
        classCategory: String? = "A",
        durationLabel: String? = "04:35 د",
    ): Visit = Visit(
        id = id,
        date = "10/05/2034",
        time = "06:54 Am",
        city = option("شبين الكوم"),
        region = option("البر الشرقى"),
        account = option(account),
        placeName = placeName,
        specialty = option(specialty),
        shift = Shift.Am,
        type = VisitType.Planned,
        visitMode = VisitMode.Single,
        classCategory = classCategory?.let(::option),
        syncStatus = SyncStatus.NotSynced,
        durationLabel = durationLabel,
    )

    fun unplanned(
        id: String = "d1",
        placeName: String = "د. هاني المسيري",
        account: String = "عيادة باطنة",
        specialty: String = "باطنة",
    ): Visit = Visit(
        id = id,
        date = "10/05/2034",
        time = "06:54 Am",
        city = option("شبين الكوم"),
        address = "\uD83D\uDCCD شارع الجلاء - برج الأطباء",
        placeName = placeName,
        account = option(account),
        specialty = option(specialty),
        shift = Shift.Pm,
        type = VisitType.Unplanned,
        syncStatus = SyncStatus.NotSynced,
    )






}
