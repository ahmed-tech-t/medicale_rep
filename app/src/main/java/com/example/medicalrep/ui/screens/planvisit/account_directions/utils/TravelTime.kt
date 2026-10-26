package com.example.medicalrep.ui.screens.planvisit.account_directions.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import com.example.medicalrep.ui.utils.MINUTES_PER_HOUR
import com.example.medicalrep.ui.utils.SECONDS_PER_MINUTE
import kotlin.math.roundToInt

private const val WALKING_SPEED_KMH = 3.7
private const val METERS_PER_KM = 1000

/** Walking time is estimated from the distance; OSRM only has the car profile. */
fun estimateWalkingSeconds(distanceMeters: Int): Int =
    (distanceMeters / (WALKING_SPEED_KMH * METERS_PER_KM / (MINUTES_PER_HOUR * SECONDS_PER_MINUTE))).roundToInt()

/** "850 m" or "13 km". */
@Composable
fun distanceText(meters: Int): String =
    if (meters >= METERS_PER_KM) {
        stringResource(R.string.distance_km_format, (meters / METERS_PER_KM.toDouble()).roundToInt())
    } else {
        stringResource(R.string.distance_m_format, meters)
    }
