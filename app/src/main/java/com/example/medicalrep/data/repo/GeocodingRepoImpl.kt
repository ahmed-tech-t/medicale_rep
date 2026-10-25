package com.example.medicalrep.data.repo

import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.utils.toApiErrorMessage
import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.data.remote.NominatimApi
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.repo.GeocodingRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GeocodingRepoImpl @Inject constructor(
    private val api: NominatimApi,
    private val appSettingsManager: AppSettingsManager,
    private val json: Json,
) : GeocodingRepo {

    override fun getAddress(point: RoutePoint): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val address = api.reverse(
                lat = point.latitude,
                lon = point.longitude,
                language = appSettingsManager.getLanguageSync(),
            ).displayName
            if (address.isNullOrBlank()) {
                emit(Resource.Error("No address found"))
            } else {
                emit(Resource.Success(address))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toApiErrorMessage(json)))
        }
    }
}
