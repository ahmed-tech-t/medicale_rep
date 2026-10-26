package com.example.medicalrep.ui.models

import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus

/** Every filter a visit list can offer; each option knows which visits it keeps. */
enum class VisitFilter(val matches: (VisitUi) -> Boolean) {
    All({ true }),
    Morning({ it.shift == Shift.Am }),
    Evening({ it.shift == Shift.Pm }),
    ClassA({ it.classCategory == "A" }),
    ClassB({ it.classCategory == "B" }),
    ClassC({ it.classCategory == "C" }),
    DoubleVisits({ it.isDoubleVisit }),
    Synced({ it.syncStatus == SyncStatus.Synced }),
    NotSynced({ it.syncStatus == SyncStatus.NotSynced }),
    ;

    /** The same filter as database parameters (null / false = ignore). */
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
