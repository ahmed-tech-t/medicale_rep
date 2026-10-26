package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType

fun AccountEntity.toDomain() = Account(
    id = id,
    backendId = backendId,
    name = name,
    entityType = entityType,
    classCategory = classCategory,
    specialty = specialty,
    city = city,
    district = district,
    address = address,
    latitude = latitude,
    longitude = longitude,
    supervisorId = supervisorId,
    syncStatus = syncStatus,
    signPhoto = signPhoto,
    createdAt = createdAt,
)

fun Account.toEntity() = AccountEntity(
    id = id,
    backendId = backendId,
    supervisorId = supervisorId,
    name = name,
    entityType = entityType,
    classCategory = classCategory,
    specialty = specialty,
    city = city,
    district = district,
    address = address,
    latitude = latitude,
    longitude = longitude,
    syncStatus = syncStatus,
    signPhoto = signPhoto,
    createdAt = createdAt,
    accountType = AccountType.fromEntityType(entityType),
    searchText = searchTextOf(name, entityType, classCategory, specialty, city, district, address),
)
