package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.SyncInfoEntity
import com.example.medicalrep.domain.models.SyncInfo

fun SyncInfoEntity.toDomain() = SyncInfo(key = key, lastUpdatedMillis = lastUpdatedMillis)

fun SyncInfo.toEntity() = SyncInfoEntity(key = key, lastUpdatedMillis = lastUpdatedMillis)
