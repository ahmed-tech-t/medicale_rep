package com.example.medicalrep.ui.preview

import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.StaticsGiftsFakes
import com.example.medicalrep.ui.models.StaticsMetricUi
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

    fun toSyncVisits(): List<Visit> = listOf(
        VisitSamples.unplanned(id = "sync-1", placeName = "د. سارة أحمد", account = "عيادة أطفال"),
        VisitSamples.unplanned(id = "sync-2", placeName = "د. كريم فؤاد", account = "عيادة جلدية"),
    )

    fun visitMetrics(): List<StaticsMetricUi> = listOf(
        StaticsMetricUi.Today(value = 26),
        StaticsMetricUi.Completed(value = 10),
        StaticsMetricUi.Clinics(value = 14),
        StaticsMetricUi.Doctors(value = 14),
    )

    /** Full Home as in Figma 33:8059: online, 2 visits to sync, active visit, today visits, stats, custody. */
    fun homeState(language: String = "ar"): HomeState = HomeState(
        user = user().toUi(),
        connection = ConnectionState.Online,
        activeVisit = activeVisit().toUi(),
        todayVisits = todayVisits().map { it.toUi() },
        toSyncVisits = toSyncVisits().map { it.toUi() },
        visitMetrics = visitMetrics(),
        custodySummary = StaticsGiftsFakes.items,
        language = language,
    )
}
