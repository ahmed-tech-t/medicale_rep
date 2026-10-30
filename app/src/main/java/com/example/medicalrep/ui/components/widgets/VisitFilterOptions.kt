package com.example.medicalrep.ui.components.widgets

import com.example.medicalrep.R
import com.example.medicalrep.ui.models.VisitFilter

/** For visits still to do: when and how important. Used by planned and planning lists. */
val plannedVisitFilterOptions = listOf(
    FilterOption(VisitFilter.All, R.string.filter_all),
    FilterOption(VisitFilter.Morning, R.string.filter_morning),
    FilterOption(VisitFilter.Evening, R.string.filter_evening),
    FilterOption(VisitFilter.ClassA, R.string.filter_class_a),
    FilterOption(VisitFilter.ClassB, R.string.filter_class_b),
    FilterOption(VisitFilter.ClassC, R.string.filter_class_c),
)
