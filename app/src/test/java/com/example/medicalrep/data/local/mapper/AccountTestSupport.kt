package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.relation.AccountWithPlace
import com.example.medicalrep.data.local.relation.DistrictWithCity
import com.example.medicalrep.domain.models.Account

/** The entity with the district and city rows a database join would add, taken from the account itself. */
fun AccountEntity.withPlaceOf(account: Account) = AccountWithPlace(
    this,
    account.district?.let { district ->
        DistrictWithCity(
            DistrictEntity(district.id, account.city?.id.orEmpty(), district.name),
            account.city?.let { CityEntity(it.id, it.name) },
        )
    },
    account.doctors.map { it.toEntity() },
)

/** Account -> Room row -> account, as the database would return it. */
fun Account.throughRoom(): Account = toEntity().withPlaceOf(this).toDomain()
