package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.SyncStatus

/** The order's own data. The account text is read from the account, never copied; see [ORDER_SEARCH_TEXT_SQL]. */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(UserEntity::class, parentColumns = ["id"], childColumns = ["repId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [Index("repId"), Index("accountId"), Index("createdAtMillis"), Index("syncStatus")],
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val repId: String?,
    val accountId: String?,
    val createdAtMillis: Long,
    val syncStatus: SyncStatus,
    val searchText: String = "",
)
