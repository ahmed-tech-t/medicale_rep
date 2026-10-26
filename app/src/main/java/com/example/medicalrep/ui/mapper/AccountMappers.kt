package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.models.AccountUi

fun Account.toUi() = AccountUi(
    id = id,
    name = name,
    entityType = entityType,
    type = AccountType.fromEntityType(entityType),
    classCategory = classCategory,
    specialty = specialty,
    city = city,
    district = district,
    address = address,
    latitude = latitude,
    longitude = longitude,
    isSynced = syncStatus == SyncStatus.Synced,
)
