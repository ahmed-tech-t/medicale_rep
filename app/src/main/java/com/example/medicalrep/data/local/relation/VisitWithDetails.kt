package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CatalogItemEntity
import com.example.medicalrep.data.local.entity.RepEntity
import com.example.medicalrep.data.local.entity.DoctorEntity
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.entity.VisitEntity

/** A visit with everything the screens show: its account, its supervisor and the samples and gifts given. */
data class VisitWithDetails(
    @Embedded val visit: VisitEntity,
    @Relation(entity = AccountEntity::class, parentColumn = "accountId", entityColumn = "id")
    val account: AccountWithPlace?,
    @Relation(parentColumn = "accompanyingRepId", entityColumn = "id")
    val supervisor: RepEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(VisitItemEntity::class, parentColumn = "visitId", entityColumn = "itemId"),
    )
    val items: List<CatalogItemEntity>,
    @Relation(parentColumn = "id", entityColumn = "visitId")
    val lines: List<VisitItemEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(VisitDoctorEntity::class, parentColumn = "visitId", entityColumn = "doctorId"),
    )
    val doctors: List<DoctorEntity>,
)
