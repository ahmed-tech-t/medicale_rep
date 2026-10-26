package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.SyncInfo
import com.example.medicalrep.ui.models.SyncInfoUi
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate

fun SyncInfo.toUi() = SyncInfoUi(
    key = key,
    lastUpdated = lastUpdatedMillis.takeIf { it > 0 }?.let { "${formatDate(it)} ${formatClockTime(it)}" }.orEmpty(),
)
