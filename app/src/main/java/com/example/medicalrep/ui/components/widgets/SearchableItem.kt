package com.example.medicalrep.ui.components.widgets

/** Anything a search field can list: it is shown (and filtered) by [name] and returned as itself when picked. */
interface SearchableItem {
    val name: String
}

/** Plain text option. */
data class TextItem(override val name: String) : SearchableItem

fun List<String>.toSearchItems(): List<TextItem> = map(::TextItem)
