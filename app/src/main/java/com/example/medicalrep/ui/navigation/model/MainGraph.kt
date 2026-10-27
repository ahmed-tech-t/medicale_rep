package com.example.medicalrep.ui.navigation.model

import kotlinx.serialization.Serializable

open class MainGraph {
    @Serializable
    data object Root : MainGraph()

    @Serializable
    data object Home : MainGraph()

    @Serializable
    data class PlanVisit(val visitId: String? = null, val isNew: Boolean = true, val resumeDraft: Boolean = false) : MainGraph()

    @Serializable
    data object DataCenter : MainGraph()

    @Serializable
    data object SampleAndGifts : MainGraph()

    @Serializable
    data object VisitLog : MainGraph()

    @Serializable
    data object Products : MainGraph()

    @Serializable
    data object Orders : MainGraph()

    @Serializable
    data object Statistics : MainGraph()

    @Serializable
    data object Accounts : MainGraph()

    @Serializable
    data object VisitPlanning : MainGraph()

    @Serializable
    data class VisitDetails(val visitId: String) : MainGraph()

    @Serializable
    data object RepTracking : MainGraph()

    @Serializable
    data object MapRepTracking : MainGraph()

    @Serializable
    data object Terms : MainGraph()

    @Serializable
    data object AddAccount : MainGraph()

    @Serializable
    data class VisitLocationMap(
        val visitLatitude: Double,
        val visitLongitude: Double,
        val accountLatitude: Double,
        val accountLongitude: Double,
        val travelSeconds: Int,
    ) : MainGraph()

    @Serializable
    data class AccountLocation(
        val accountLatitude: Double,
        val accountLongitude: Double,
        val accountName: String,
        val address: String,
        val classCategory: String,
        val accountType: String,
        val specialty: String,
    ) : MainGraph()

    @Serializable
    data class AccountDirections(
        val accountLatitude: Double,
        val accountLongitude: Double,
        val accountName: String,
    ) : MainGraph()
}
