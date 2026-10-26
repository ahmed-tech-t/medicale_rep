package com.example.medicalrep.domain.models

/** The one visit the rep saved to finish later; [step] is the form step he left it on (0 = first). */
data class DraftVisit(
    val step: Int,
    val visit: Visit,
)
