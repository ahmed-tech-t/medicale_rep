package com.example.medicalrep.ui.screens.addaccount.utils

import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes

/** Sample options and the fake user location, shown until the real data sources exist. */
object AddAccountFakes {

    fun state(): AddAccountState {
        val options = PlanVisitFakes.state()
        return AddAccountState(
            userLocation = GeoPoint(latitude = 30.0450, longitude = 31.2365),
            cityOptions = options.cityOptions.map { it.name },
            districtOptions = options.districtOptions.map { it.name },
            entityTypeOptions = options.entityTypeOptions.map { it.name },
            classOptions = listOf("A", "B", "C"),
            specialtyOptions = listOf("باطنة وجهاز هضمي", "أطفال", "جلدية"),
        )
    }
}
