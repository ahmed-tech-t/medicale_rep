package com.example.medicalrep.domain.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Answers [resultFor] each point; set [gate] to keep lookups waiting. [answered] lists the points whose answer was given. */
class FakeGeocodingRepo : GeocodingRepo {
    var resultFor: (GeoPoint) -> Resource<String> = { Resource.Success("Al Geish Street") }
    var gate: CompletableDeferred<Unit>? = null
    val requested = mutableListOf<GeoPoint>()
    val answered = mutableListOf<GeoPoint>()

    override fun getAddress(point: GeoPoint): Flow<Resource<String>> = flow {
        requested += point
        emit(Resource.Loading())
        gate?.await()
        answered += point
        emit(resultFor(point))
    }
}
