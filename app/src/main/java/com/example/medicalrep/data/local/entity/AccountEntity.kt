package com.example.medicalrep.data.local.entity

import androidx.room.ColumnInfo
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
            entity = RepEntity::class,
            parentColumns = ["id"],
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = DistrictEntity::class,
            parentColumns = ["id"],
            childColumns = ["districtId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("districtId"), Index("supervisorId"), Index("backendId"), Index("syncStatus"), Index("accountType")],
)
data class AccountEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val supervisorId: String?,
    val name: String,
    val entityType: String,
    /** The district the account is in; its city comes from the district. */
    val districtId: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val syncStatus: SyncStatus,
    val signPhoto: String?,
    val createdAt: Long,
    /** Read from [entityType] when the account is saved, so the list can be filtered by type in the database. */
    val accountType: AccountType? = null,
    /** The key of a [com.example.medicalrep.domain.models.WorkingTime]. */
    @ColumnInfo(defaultValue = "Both") val workingTime: String = "Both",
    val searchText: String = "",
)
