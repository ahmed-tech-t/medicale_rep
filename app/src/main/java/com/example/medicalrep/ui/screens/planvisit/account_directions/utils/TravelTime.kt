package com.example.medicalrep.ui.screens.planvisit.account_directions.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import kotlin.math.roundToInt

private const val WALKING_SPEED_KMH = 3.7
private const val SECONDS_PER_MINUTE = 60
private const val MINUTES_PER_HOUR = 60
private const val METERS_PER_KM = 1000

/** Walking time is estimated from the distance; OSRM only has the car profile. */
fun estimateWalkingSeconds(distanceMeters: Int): Int =
    (distanceMeters / (WALKING_SPEED_KMH * METERS_PER_KM / (MINUTES_PER_HOUR * SECONDS_PER_MINUTE))).roundToInt()

/** "25 min" or "3 h 31 min". */
@Composable
fun durationText(seconds: Int): String {
    val minutes = (seconds / SECONDS_PER_MINUTE.toDouble()).roundToInt().coerceAtLeast(1)
    return if (minutes < MINUTES_PER_HOUR) {
        stringResource(R.string.duration_minutes, minutes)
    } else {
        stringResource(R.string.duration_hours_minutes, minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)
    }
}

/** "850 m" or "13 km". */
@Composable
fun distanceText(meters: Int): String =
    if (meters >= METERS_PER_KM) {
        stringResource(R.string.distance_km_format, (meters / METERS_PER_KM.toDouble()).roundToInt())
    } else {
        stringResource(R.string.distance_m_format, meters)
    }
