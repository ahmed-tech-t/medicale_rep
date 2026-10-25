package com.example.medicalrep.ui.screens.visitdetails.utils

import com.example.medicalrep.ui.models.VisitUi
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** A visit recorded within this distance of the account counts as made at the account. */
const val MAX_MATCH_DISTANCE_METERS = 50

private const val EARTH_RADIUS_METERS = 6_371_000.0

/** Great-circle (haversine) distance between two coordinates, rounded to whole meters. */
fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return (2 * EARTH_RADIUS_METERS * asin(sqrt(a))).roundToInt()
}

fun isAtAccountLocation(distanceMeters: Int): Boolean = distanceMeters <= MAX_MATCH_DISTANCE_METERS

/** Both coordinates are known; (0.0, 0.0) is the "not recorded" default. */
val VisitUi.hasLocations: Boolean
    get() = (visitLatitude != 0.0 || visitLongitude != 0.0) &&
        (accountLatitude != 0.0 || accountLongitude != 0.0)

val VisitUi.distanceFromAccountMeters: Int
    get() = distanceMeters(visitLatitude, visitLongitude, accountLatitude, accountLongitude)
