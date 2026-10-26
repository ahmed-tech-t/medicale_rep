package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** The saved location for ViewModel tests: set [location], hold readers with [gate], count reads in [calls]. */
class FakeLastLocationRepo(initial: GeoPoint? = null) : LastLocationRepo {
    private val flow = MutableStateFlow(initial)
    var location: GeoPoint?
        get() = flow.value
        set(value) {
            flow.value = value
        }
    var gate: CompletableDeferred<Unit>? = null
    var calls = 0
    val saved = mutableListOf<Pair<GeoPoint, Long>>()

    override val lastLocation: Flow<GeoPoint?> = flow

    override suspend fun current(): GeoPoint? {
        calls++
        gate?.await()
        return flow.value
    }

    override suspend fun save(location: GeoPoint, timeMillis: Long) {
        saved += location to timeMillis
        flow.value = location
    }
}
