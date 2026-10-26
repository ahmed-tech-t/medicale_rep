package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.SyncStatus

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("supervisorId"), Index("backendId"), Index("syncStatus"), Index("accountType")],
)
data class AccountEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val supervisorId: String?,
    val name: String,
    val entityType: String,
    val classCategory: String,
    val specialty: String,
    val city: String,
    val district: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val syncStatus: SyncStatus,
    val signPhoto: String?,
    val createdAt: Long,
    /** Read from [entityType] when the account is saved, so the list can be filtered by type in the database. */
    val accountType: AccountType? = null,
    val searchText: String = "",
)
