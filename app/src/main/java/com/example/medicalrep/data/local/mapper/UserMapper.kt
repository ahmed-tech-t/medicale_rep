package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.UserEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.User

fun UserEntity.toDomain() = User(
    id = id,
    displayName = displayName,
    roleTitle = roleTitle,
    specialty = specialty,
    employeeCode = employeeCode,
    deviceId = deviceId,
    initials = initials,
    role = role,
    supervisorId = supervisorId,
)

fun User.toEntity() = UserEntity(
    id = id,
    role = role,
    supervisorId = supervisorId,
    displayName = displayName,
    roleTitle = roleTitle,
    specialty = specialty,
    employeeCode = employeeCode,
    deviceId = deviceId,
    initials = initials,
    searchText = searchTextOf(displayName, roleTitle, specialty, employeeCode),
)
