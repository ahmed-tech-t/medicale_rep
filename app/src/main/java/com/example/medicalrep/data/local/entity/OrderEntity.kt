package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.OrderStatus
import com.example.medicalrep.domain.models.SyncStatus

/** The order's own data. The account text is read from the account, never copied; see [ORDER_SEARCH_TEXT_SQL]. */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DoctorEntity::class, parentColumns = ["id"], childColumns = ["doctorId"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [Index("repId"), Index("accountId"), Index("doctorId"), Index("createdAtMillis"), Index("syncStatus")],
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val repId: String?,
    val accountId: String?,
    val createdAtMillis: Long,
    val syncStatus: SyncStatus,
    val doctorId: String? = null,
    val status: OrderStatus = OrderStatus.Pending,
    /** The sum of the lines, kept so a list needs no lines to show it. */
    val totalAmount: Double = 0.0,
    /** The number the rep sees; set when the order is first saved. */
    val number: Int = 0,
    val searchText: String = "",
)
