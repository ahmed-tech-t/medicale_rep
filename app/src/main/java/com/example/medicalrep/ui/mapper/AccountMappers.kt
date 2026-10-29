package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.models.AccountUi

fun Account.toUi() = AccountUi(
    id = id,
    name = name,
    accountType = accountType?.toUi(),
    type = AccountType.fromEntityType(accountType?.name.orEmpty()),
    classCategory = classCategory?.toUi(),
    specialty = specialty?.toUi(),
    city = city?.toUi(),
    district = district?.toUi(),
    address = address,
    latitude = latitude,
    longitude = longitude,
    isSynced = syncStatus == SyncStatus.Synced,
)
