package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.WorkingTime

fun AccountEntity.toDomain() = Account(
    id = id,
    backendId = backendId,
    name = name,
    accountType = entityType.toBasicModel(),
    classCategory = classCategory.toBasicModel(),
    specialty = specialty.toBasicModel(),
    city = city.toBasicModel(),
    district = district.toBasicModel(),
    workingTime = WorkingTime.fromKey(workingTime),
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
    entityType = accountType?.name.orEmpty(),
    classCategory = classCategory?.name.orEmpty(),
    specialty = specialty?.name.orEmpty(),
    city = city?.name.orEmpty(),
    district = district?.name.orEmpty(),
    workingTime = workingTime.key,
    address = address,
    latitude = latitude,
    longitude = longitude,
    syncStatus = syncStatus,
    signPhoto = signPhoto,
    createdAt = createdAt,
    accountType = AccountType.fromEntityType(accountType?.name.orEmpty()),
    searchText = searchTextOf(name, accountType?.name, classCategory?.name, specialty?.name, city?.name, district?.name, address),
)

/** The account columns are plain text for now, so the text is also the id; blank text means not set. */
private fun String.toBasicModel() = takeIf { it.isNotBlank() }?.let { BasicModel(it, it) }
