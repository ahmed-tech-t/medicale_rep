package com.example.medicalrep.ui.common.location

import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface LocationHelper {
    fun status(): LocationStatus

    /** Emits when the system changes location settings (GPS switched on or off) while the app is open. */
    fun statusChanges(): Flow<Unit> = emptyFlow()

    /** GPS positions while collected, one each time the phone moves about 5 m (needs the permission and GPS on). */
    fun updates(): Flow<GeoPoint>
}
