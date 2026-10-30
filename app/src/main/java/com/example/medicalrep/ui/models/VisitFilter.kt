package com.example.medicalrep.ui.models

import com.example.medicalrep.domain.models.Shift

/** Every filter the planning list can offer, as the database parameters it stands for (null / false = ignore). */
enum class VisitFilter {
    All,
    Morning,
    Evening,
    ClassA,
    ClassB,
    ClassC,
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
}
