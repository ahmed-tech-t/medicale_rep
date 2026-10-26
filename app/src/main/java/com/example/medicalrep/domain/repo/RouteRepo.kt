package com.example.medicalrep.domain.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import kotlinx.coroutines.flow.Flow

interface RouteRepo {
    /** Emits Loading, then the road route (line, distance, driving time) through [waypoints] in order (at least 2), or an Error. */
    fun getRoute(waypoints: List<GeoPoint>): Flow<Resource<RouteResult>>
}
