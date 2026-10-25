package com.example.medicalrep.domain.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.RoutePoint
import kotlinx.coroutines.flow.Flow

interface RouteRepo {
    /** Emits Loading, then the road route through [waypoints] in order (at least 2), or an Error. */
    fun getRoute(waypoints: List<RoutePoint>): Flow<Resource<List<RoutePoint>>>
}
