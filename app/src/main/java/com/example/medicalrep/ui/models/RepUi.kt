package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.components.widgets.SearchableItem

/** A rep as the supervisor picks him: listed and filtered by [displayName]. */
@Immutable
data class RepUi(
    val id: String,
    val displayName: String,
    val employeeCode: String = "",
) : SearchableItem {
    override val name: String get() = displayName
}
