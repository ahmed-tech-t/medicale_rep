package com.example.medicalrep.ui.components.widgets.filter

sealed interface ClassFilter {
    val label: String

    data object All : ClassFilter {
        override val label = "All"
    }

    data object A : ClassFilter {
        override val label = "A"
    }

    data object B : ClassFilter {
        override val label = "B"
    }

    data object C : ClassFilter {
        override val label = "C"
    }

    companion object {
        val entries: List<ClassFilter> = listOf(All, A, B, C)
    }
}