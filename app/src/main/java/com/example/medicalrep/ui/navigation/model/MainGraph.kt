package com.example.medicalrep.ui.navigation.model

import kotlinx.serialization.Serializable

open class MainGraph {
    @Serializable
    data object Root : MainGraph()

    @Serializable
    data object Home : MainGraph()

    @Serializable
    data class PlanVisit(val visitId: String? = null,val isNew: Boolean = true) : MainGraph()

    @Serializable
    data object DataCenter : MainGraph()

    @Serializable
    data object SampleAndGifts : MainGraph()

    @Serializable
    data object VisitLog : MainGraph()

    @Serializable
    data object Products : MainGraph()

    @Serializable
    data object VisitPlanning : MainGraph()

    @Serializable
    data class VisitDetails(val visitId: String) : MainGraph()

    @Serializable
    data object RepTracking : MainGraph()
}
