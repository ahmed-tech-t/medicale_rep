package com.example.medicalrep.ui.screens.addaccount.utils

import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.screens.visit_location_map.utils.VisitLocationMapFakes

/** Sample options and the fake user location, shown until the real data sources exist. */
object AddAccountFakes {

    fun state(): AddAccountState {
        val options = PlanVisitFakes.state()
        return AddAccountState(
            userLocation = VisitLocationMapFakes.userLocation,
            cityOptions = options.cityOptions,
            districtOptions = options.districtOptions,
            entityTypeOptions = options.entityTypeOptions,
            classOptions = listOf("A", "B", "C"),
            specialtyOptions = options.specialtyOptions,
        )
    }
}
