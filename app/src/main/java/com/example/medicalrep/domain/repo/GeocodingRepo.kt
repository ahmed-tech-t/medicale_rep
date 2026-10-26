package com.example.medicalrep.domain.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.flow.Flow

interface GeocodingRepo {
    /** Emits Loading, then the street name of [point], or an Error when it is unknown. */
    fun getAddress(point: GeoPoint): Flow<Resource<String>>
}
