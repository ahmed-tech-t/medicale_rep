package com.example.medicalrep.ui.models

import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus

/** Every filter a visit list can offer, as the database parameters it stands for (null / false = ignore). */
enum class VisitFilter {
    All,
    Morning,
    Evening,
    ClassA,
    ClassB,
    ClassC,
    DoubleVisits,
    Synced,
    NotSynced,
    ;

    val shift: Shift?
        get() = when (this) {
            Morning -> Shift.Am
            Evening -> Shift.Pm
            else -> null
        }
    val classCategory: String?
        get() = when (this) {
            ClassA -> "A"
            ClassB -> "B"
            ClassC -> "C"
            else -> null
        }
    val doubleOnly: Boolean get() = this == DoubleVisits
    val syncStatus: SyncStatus?
        get() = when (this) {
            Synced -> SyncStatus.Synced
            NotSynced -> SyncStatus.NotSynced
            else -> null
        }
}
