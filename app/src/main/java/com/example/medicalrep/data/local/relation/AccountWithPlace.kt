package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.medicalrep.data.local.entity.AccountDoctorEntity
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.entity.DoctorEntity

/** A district with its city. */
data class DistrictWithCity(
    @Embedded val district: DistrictEntity,
    @Relation(parentColumn = "cityId", entityColumn = "id")
    val city: CityEntity?,
)

/** An account with the district and city it is in and its doctors. */
data class AccountWithPlace(
    @Embedded val account: AccountEntity,
    @Relation(entity = DistrictEntity::class, parentColumn = "districtId", entityColumn = "id")
    val place: DistrictWithCity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(AccountDoctorEntity::class, parentColumn = "accountId", entityColumn = "doctorId"),
    )
    val doctors: List<DoctorEntity>,
)
