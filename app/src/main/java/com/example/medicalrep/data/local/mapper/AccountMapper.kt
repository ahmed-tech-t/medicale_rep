package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.relation.AccountWithPlace
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.WorkingTime

fun AccountWithPlace.toDomain() = Account(
    id = account.id,
    backendId = account.backendId,
    name = account.name,
    accountType = account.entityType.toBasicModel(),
    city = place?.city?.let { BasicModel(it.id, it.name) },
    district = place?.district?.let { BasicModel(it.id, it.name) },
    workingTime = WorkingTime.fromKey(account.workingTime),
    address = account.address,
    latitude = account.latitude,
    longitude = account.longitude,
    supervisorId = account.supervisorId,
    syncStatus = account.syncStatus,
    signPhoto = account.signPhoto,
    createdAt = account.createdAt,
    doctors = doctors.map { it.toDomain() },
)

fun Account.toEntity() = AccountEntity(
    id = id,
    backendId = backendId,
    supervisorId = supervisorId,
    name = name,
    entityType = accountType?.name.orEmpty(),
    districtId = district?.id,
    workingTime = workingTime.key,
    address = address,
    latitude = latitude,
    longitude = longitude,
    syncStatus = syncStatus,
    signPhoto = signPhoto,
    createdAt = createdAt,
    accountType = AccountType.fromEntityType(accountType?.name.orEmpty()),
    searchText = searchTextOf(name, accountType?.name, city?.name, district?.name, address),
)

/** The type column is plain text for now, so the text is also the id; blank text means not set. */
private fun String.toBasicModel() = takeIf { it.isNotBlank() }?.let { BasicModel(it, it) }
