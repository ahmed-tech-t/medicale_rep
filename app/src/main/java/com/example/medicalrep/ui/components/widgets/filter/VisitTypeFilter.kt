package com.example.medicalrep.ui.components.widgets.filter

import androidx.annotation.StringRes
import com.example.medicalrep.R

/** Which kind of visit (alone or with a supervisor) to list. */
sealed interface VisitTypeFilter {
    @get:StringRes
    val valueRes: Int

    data object All : VisitTypeFilter {
        override val valueRes = R.string.filter_all
    }

    data object Single : VisitTypeFilter {
        override val valueRes = R.string.filter_single_visit
    }

    data object Double : VisitTypeFilter {
        override val valueRes = R.string.filter_double_visit
    }

    companion object {
        val entries: List<VisitTypeFilter> = listOf(All, Single, Double)
    }
}