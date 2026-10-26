package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.ui.models.TrackPointUi
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate

fun TrackPoint.toUi() = TrackPointUi(
    id = id,
    latitude = latitude,
    longitude = longitude,
    date = formatDate(timeMillis),
    time = formatClockTime(timeMillis),
    isSynced = syncStatus == SyncStatus.Synced,
)
