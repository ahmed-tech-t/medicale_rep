package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CustodyEntity
import com.example.medicalrep.data.local.entity.UserEntity
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.entity.VisitEntity

/** A visit with everything the screens show: its account, its supervisor and the samples and gifts given. */
data class VisitWithDetails(
    @Embedded val visit: VisitEntity,
    @Relation(parentColumn = "accountId", entityColumn = "id")
    val account: AccountEntity?,
    @Relation(parentColumn = "supervisorId", entityColumn = "id")
    val supervisor: UserEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(VisitCustodyEntity::class, parentColumn = "visitId", entityColumn = "custodyId"),
    )
    val custody: List<CustodyEntity>,
    @Relation(parentColumn = "id", entityColumn = "visitId")
    val lines: List<VisitCustodyEntity>,
)
