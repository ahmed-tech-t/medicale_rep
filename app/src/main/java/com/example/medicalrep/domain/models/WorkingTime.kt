package com.example.medicalrep.domain.models

/** The periods an account is open for visits; [Both] lets the rep choose. */
sealed interface WorkingTime {
    /** The text stored in Room. */
    val key: String

    /** The shifts a visit to this account can use. */
    val shifts: List<Shift>

    /** The only shift a visit can use, or null when the rep chooses. */
    val onlyShift: Shift? get() = shifts.singleOrNull()

    data object Am : WorkingTime {
        override val key = "Am"
        override val shifts = listOf(Shift.Am)
    }

    data object Pm : WorkingTime {
        override val key = "Pm"
        override val shifts = listOf(Shift.Pm)
    }

    data object Both : WorkingTime {
        override val key = "Both"
        override val shifts = listOf(Shift.Am, Shift.Pm)
    }

    companion object {
        val entries: List<WorkingTime> get() = listOf(Am, Pm, Both)

        /** Anything unknown or blank reads as [Both], so an account without a value stays open to both periods. */
        fun fromKey(key: String?): WorkingTime = entries.firstOrNull { it.key == key } ?: Both
    }
}
