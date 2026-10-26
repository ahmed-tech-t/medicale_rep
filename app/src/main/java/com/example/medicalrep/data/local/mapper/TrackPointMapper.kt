package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.TrackPointEntity
import com.example.medicalrep.domain.models.TrackPoint

fun TrackPointEntity.toDomain() = TrackPoint(
    id = id,
    backendId = backendId,
    repId = repId,
    visitId = visitId,
    latitude = latitude,
    longitude = longitude,
    timeMillis = timeMillis,
    syncStatus = syncStatus,
)

fun TrackPoint.toEntity() = TrackPointEntity(
    id = id,
    backendId = backendId,
    repId = repId,
    visitId = visitId,
    latitude = latitude,
    longitude = longitude,
    timeMillis = timeMillis,
    syncStatus = syncStatus,
)
