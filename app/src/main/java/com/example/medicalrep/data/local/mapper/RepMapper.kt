package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.RepEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Rep

fun RepEntity.toDomain() = Rep(
    id = id,
    displayName = displayName,
    employeeCode = employeeCode,
    deviceId = deviceId,
    role = role,
    supervisorId = supervisorId,
)

fun Rep.toEntity() = RepEntity(
    id = id,
    role = role,
    supervisorId = supervisorId,
    displayName = displayName,
    employeeCode = employeeCode,
    deviceId = deviceId,
    searchText = searchTextOf(displayName, employeeCode),
)
