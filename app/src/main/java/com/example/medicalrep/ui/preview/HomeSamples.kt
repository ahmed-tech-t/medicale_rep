package com.example.medicalrep.ui.preview

import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.screens.home.main.HomeState

/** Shared Home sample data for Previews, ViewModel fake state, and tests. */
object HomeSamples {

    fun user(
        id: String = "u1",
        displayName: String = "أحمد محمد",
        roleTitle: String = "ميديكال ريب",
        specialty: String = "أخصائي دعاية طبية",
        employeeCode: String = "9842-EG",
        deviceId: String = "99201",
        initials: String = "أم",
    ): User = User(
        id = id,
        displayName = displayName,
        roleTitle = roleTitle,
        specialty = specialty,
        employeeCode = employeeCode,
        deviceId = deviceId,
        initials = initials,
    )

    fun activeVisit(): Visit = VisitSamples.planned(
        id = "active-1",
        placeName = "عيادة د. محمود عبد الرحمن",
        specialty = "استشاري باطنة",
        classCategory = "A",
        durationLabel = "04:35 د",
    )

    fun todayVisits(): List<Visit> = listOf(
        VisitSamples.unplanned(id = "today-1", placeName = "د. هاني المسيري", account = "عيادة باطنة"),
        VisitSamples.unplanned(id = "today-2", placeName = "د. هاني المسيري", account = "عيادة باطنة"),
    )

    /** Full Home as in the design: online, a pending visit, 2 visits to upload, the week's progress and today's visits. */
    fun homeState(language: String = "ar"): HomeState = HomeState(
        user = user().toUi(),
        connection = ConnectionState.Online,
        address = "Stockton St, Union Square",
        dateMillis = 1_792_000_000_000L,
        progress = VisitProgress(completed = 10, planned = 26),
        activeVisit = activeVisit().toUi(),
        todayVisits = todayVisits().map { it.toUi() },
        toSyncCount = 2,
        language = language,
    )
}
