package com.example.medicalrep.domain.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.RoutePoint
import kotlinx.coroutines.flow.Flow

interface GeocodingRepo {
    /** Emits Loading, then the readable address of [point], or an Error. */
    fun getAddress(point: RoutePoint): Flow<Resource<String>>
}
