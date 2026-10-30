package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.ExecutionStatus
import com.example.medicalrep.domain.models.VisitType

/** The visit's own data. Account details (city, address, place name, ...) are read from the account, never copied. */
@Entity(
    tableName = "visits",
    foreignKeys = [
        ForeignKey(RepEntity::class, parentColumns = ["id"], childColumns = ["accompanyingRepId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.SET_NULL),
    ],
    indices = [
        Index("repId"),
        Index("accompanyingRepId"),
        Index("accountId"),
        Index("backendId"),
        Index("dateTimeMillis"),
        Index("type"),
        Index("syncStatus"),
        Index("approvalStatus"),
        Index("executionStatus"),
        Index("completedAtMillis"),
    ],
)
data class VisitEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val repId: String?,
    /** The supervisor who goes with the rep. */
    val accompanyingRepId: String?,
    val accountId: String?,
    /** 0 when unknown. */
    val dateTimeMillis: Long,
    val shift: Shift,
    val type: VisitType,
    val visitMode: VisitMode,
    /** Where the rep was when he did the visit. */
    val visitLatitude: Double,
    val visitLongitude: Double,
    val travelSeconds: Int,
    val imageNotes: List<String>,
    val voiceNotePath: String?,
    val syncStatus: SyncStatus,
    val durationLabel: String,
    val approvalStatus: ApprovalStatus,
    val executionStatus: ExecutionStatus,
    /** Why a cancelled visit was cancelled. */
    val cancelReason: String?,
    val textNote: String?,
    /** The plan request this visit came from. */
    val requestId: String?,
    val requestedAtMillis: Long?,
    val decidedAtMillis: Long?,
    /** When the visit was finished; null while it is still to do. */
    val completedAtMillis: Long?,
    /** Made by the DAO from the account, the supervisor and the date; see [VISIT_SEARCH_TEXT_SQL]. */
    val searchText: String = "",
)
