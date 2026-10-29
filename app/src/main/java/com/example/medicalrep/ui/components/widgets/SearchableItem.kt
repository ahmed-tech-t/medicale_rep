package com.example.medicalrep.ui.components.widgets

import com.example.medicalrep.ui.models.BasicModelUi

/** Anything a search field can list: it is shown (and filtered) by [name] and returned as itself when picked. */
interface SearchableItem {
    val name: String
}

fun List<String>.toSearchItems(): List<BasicModelUi> = map { BasicModelUi(it, it) }
