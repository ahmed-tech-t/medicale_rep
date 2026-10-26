package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.flow.Flow

/** The user's last known location: the one place the app reads the location from. Kept up to date by GPS updates. */
interface LastLocationRepo {
    val lastLocation: Flow<GeoPoint?>

    /** Null until the first GPS update after login. */
    suspend fun current(): GeoPoint?

    suspend fun save(location: GeoPoint, timeMillis: Long)
}
