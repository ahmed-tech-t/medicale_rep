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
}
