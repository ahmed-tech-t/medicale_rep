package com.example.medicalrep.data.repo

import com.appcore.core.data.utils.toApiErrorMessage
import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.data.remote.OsrmApi
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.repo.RouteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RouteRepoImpl @Inject constructor(
    private val api: OsrmApi,
    private val json: Json,
) : RouteRepo {

    override fun getRoute(waypoints: List<RoutePoint>): Flow<Resource<List<RoutePoint>>> = flow {
        emit(Resource.Loading())
        try {
            val coordinates = waypoints.joinToString(";") { "${it.longitude},${it.latitude}" }
            val response = api.getRoute(coordinates)
            val line = response.routes.firstOrNull()?.geometry?.coordinates
            if (response.code != "Ok" || line.isNullOrEmpty()) {
                emit(Resource.Error("No route found"))
            } else {
                emit(Resource.Success(line.map { RoutePoint(latitude = it[1], longitude = it[0]) }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toApiErrorMessage(json)))
        }
    }
}
