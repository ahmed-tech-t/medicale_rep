package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.components.widgets.SearchableItem

@Immutable
data class BasicModelUi(
    val id: String,
    override val name: String,
) : SearchableItem
