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
    val voiceNote: String? = null,
    val syncStatus: SyncStatus? = null,
    val durationLabel: String? = null,
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