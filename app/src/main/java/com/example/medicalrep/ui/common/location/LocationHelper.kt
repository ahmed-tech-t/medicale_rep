package com.example.medicalrep.ui.common.location

import com.example.medicalrep.domain.models.map.MapCameraTarget

interface LocationHelper {
    fun status(): LocationStatus

    /** The current GPS position, or the last known one when there is no fix; null without permission or any location. */
    suspend fun getLocation(): MapCameraTarget?
}
