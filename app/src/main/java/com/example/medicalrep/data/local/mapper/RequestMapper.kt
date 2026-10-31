package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.NotificationEntity
import com.example.medicalrep.data.local.entity.RepRequestEntity
import com.example.medicalrep.data.local.entity.SystemSettingEntity
import com.example.medicalrep.domain.models.Notification
import com.example.medicalrep.domain.models.RepRequest
import com.example.medicalrep.domain.models.SystemSetting

fun RepRequestEntity.toDomain() = RepRequest(id, repId, dateMillis, category, subType, shift, status, notes, createdAtMillis, decidedAtMillis)

fun RepRequest.toEntity() = RepRequestEntity(id, repId, dateMillis, category, subType, shift, status, notes, createdAtMillis, decidedAtMillis)

fun NotificationEntity.toDomain() = Notification(id, repId, title, message, type, createdAtMillis)

fun Notification.toEntity() = NotificationEntity(id, repId, title, message, type, createdAtMillis)

fun SystemSetting.toEntity() = SystemSettingEntity(key, value)
