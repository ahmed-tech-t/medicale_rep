package com.example.medicalrep.ui.common.location

import com.example.medicalrep.domain.models.map.MapCameraTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface LocationHelper {
    fun status(): LocationStatus

    /** Emits when the system changes location settings (GPS switched on or off) while the app is open. */
    fun statusChanges(): Flow<Unit> = emptyFlow()

    /** The current GPS position, or the last known one when there is no fix; null without permission or any location. */
    suspend fun getLocation(): MapCameraTarget?
}
