package com.example.medicalrep.domain.models

import com.appcore.core.data.local.datastore.model.UserDS

/** Where the user was last seen; null until the first GPS update after login. */
val UserDS.lastLocation: GeoPoint?
    get() {
        val latitude = lastLatitude
        val longitude = lastLongitude
        return if (latitude != null && longitude != null) GeoPoint(latitude, longitude) else null
    }

fun UserDS.withLocation(fix: LocationFix, timeMillis: Long): UserDS = copy(
    lastLatitude = fix.point.latitude,
    lastLongitude = fix.point.longitude,
    lastAccuracyMeters = fix.accuracyMeters,
    lastLocationTimeMillis = timeMillis,
)
