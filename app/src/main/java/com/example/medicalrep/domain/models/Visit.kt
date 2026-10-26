package com.example.medicalrep.domain.models

import androidx.annotation.StringRes
import com.example.medicalrep.R
data class Visit(
    val id: String? = null,
    val date: String? = null,
    val time: String? = null,
    val city: String? = null,
    val region: String? = null,
    val address: String? = null,
    val account: String? = null,
    val placeName: String? = null,
    val specialty: String? = null,
    val shift: Shift? = null,
    val type: VisitType? = null,
    val visitMode: VisitMode? = null,
    val supervisorName: String? = null,
    val classCategory: String? = null,
    val gifts: List<Product>? = null,
    val products: List<Product>? = null,
    val voiceNote: ByteArray? = null,
    val syncStatus: SyncStatus? = null,
    val durationLabel: String? = null,
    val backendId: String? = null,
    val accountId: String? = null,
    val repId: String? = null,
    val supervisorId: String? = null,
    /** When the visit happened or is planned for; the text fields below are made from it when they are missing. */
    val dateTimeMillis: Long? = null,
    val visitLatitude: Double = 0.0,
    val visitLongitude: Double = 0.0,
    val accountLatitude: Double = 0.0,
    val accountLongitude: Double = 0.0,
    /** How long the rep took to reach the visit; 0 when unknown. */
    val travelSeconds: Int = 0,
    val noIssueFound: Boolean = true,
    val imageNote: String? = null,
    /** Where the saved voice note file is; [voiceNote] holds the bytes of one that is not saved yet. */
    val voiceNotePath: String? = null,
    val approvalStatus: ApprovalStatus? = null,
    val requestedAtMillis: Long? = null,
    val decidedAtMillis: Long? = null,
    val decisionNote: String? = null,
    /** When the visit was finished; null while it is still to do. */
    val completedAtMillis: Long? = null,
)

enum class Shift {
    Am,
    Pm,
}


sealed class VisitType {
    data object Planning : VisitType()
    data object Planned : VisitType()
    data object Unplanned : VisitType()
}


sealed class SyncStatus {
    data object Synced : SyncStatus()
    data object NotSynced : SyncStatus()
}
sealed class VisitMode() {
    data object Single : VisitMode()
    data object Double : VisitMode()
}