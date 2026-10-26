package com.example.medicalrep.ui.screens.planvisit.account_directions.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.ui.screens.planvisit.account_directions.utils.estimateWalkingSeconds
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceMeters
import com.example.medicalrep.ui.screens.visitdetails.utils.isAtAccountLocation

@Immutable
data class AccountDirectionsState(
    val accountLocation: GeoPoint,
    val accountName: String,
    val userLocation: GeoPoint? = null,
    val route: RouteResult? = null,
    val userStreet: String? = null,
    val capturedAtMillis: Long? = null,
    val isLocating: Boolean = false,
    val isCardExpanded: Boolean = true,
) {
    /** Straight-line distance; null until the rep's position is known. */
    val distanceToAccountMeters: Int?
        get() = userLocation?.let {
            distanceMeters(it.latitude, it.longitude, accountLocation.latitude, accountLocation.longitude)
        }

    val isAtAccount: Boolean get() = distanceToAccountMeters?.let(::isAtAccountLocation) == true

    /** There is somewhere to go: the rep's position is known and he is not at the account yet. */
    val showTravel: Boolean get() = userLocation != null && !isAtAccount

    /** Road distance; null without a route (offline or failed), so no travel times are shown. */
    val travelDistanceMeters: Int? get() = if (showTravel) route?.distanceMeters else null

    val carSeconds: Int? get() = if (showTravel) route?.durationSeconds else null

    val walkingSeconds: Int? get() = travelDistanceMeters?.let(::estimateWalkingSeconds)

    /**
     * The road route when it is loaded; otherwise (offline, failed, still loading) a straight line from the rep
     * to the account. No line within range of the account or without the rep's position.
     */
    val lineToAccount: List<GeoPoint>
        get() = when {
            userLocation == null || isAtAccount -> emptyList()
            route != null -> route.points
            else -> listOf(
                GeoPoint(userLocation.latitude, userLocation.longitude),
                GeoPoint(accountLocation.latitude, accountLocation.longitude),
            )
        }
}
