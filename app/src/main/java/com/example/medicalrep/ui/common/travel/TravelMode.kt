package com.example.medicalrep.ui.common.travel

import com.example.medicalrep.ui.utils.METERS_PER_KM

/** At most this average speed (km/h) means the rep walked. */
const val MAX_WALKING_SPEED_KMH = 6.0

private const val SECONDS_PER_HOUR = 3600.0

enum class TravelMode { Car, Foot }

/** Car or on foot, from how far the rep moved ([distanceMeters]) in [seconds]. */
fun travelModeOf(distanceMeters: Int, seconds: Int): TravelMode {
    val speedKmh = distanceMeters / METERS_PER_KM.toDouble() / (seconds / SECONDS_PER_HOUR)
    return if (speedKmh <= MAX_WALKING_SPEED_KMH) TravelMode.Foot else TravelMode.Car
}
