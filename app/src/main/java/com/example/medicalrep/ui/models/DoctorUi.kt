package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.components.widgets.SearchableItem

/** A doctor as the visit form lists and shows it. */
@Immutable
data class DoctorUi(
    val id: String,
    override val name: String,
    val classCategory: BasicModelUi? = null,
) : SearchableItem
