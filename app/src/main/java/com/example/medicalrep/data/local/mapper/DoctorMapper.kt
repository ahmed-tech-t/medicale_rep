package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.DoctorEntity
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Doctor

fun DoctorEntity.toDomain() = Doctor(
    id = id,
    backendId = backendId,
    name = name,
    classCategory = classCategory.toBasicModel(),
    specialty = specialty.toBasicModel(),
    syncStatus = syncStatus,
)

fun Doctor.toEntity() = DoctorEntity(
    id = id,
    backendId = backendId,
    name = name,
    classCategory = classCategory?.name.orEmpty(),
    specialty = specialty?.name.orEmpty(),
    syncStatus = syncStatus,
)

/** The class and specialty columns are plain text for now, so the text is also the id; blank means not set. */
private fun String.toBasicModel() = takeIf { it.isNotBlank() }?.let { BasicModel(it, it) }
