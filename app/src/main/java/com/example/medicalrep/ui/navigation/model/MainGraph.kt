package com.example.medicalrep.ui.navigation.model

import kotlinx.serialization.Serializable

open class MainGraph {
    @Serializable
    data object Root : MainGraph()

    @Serializable
    data object Home : MainGraph()
}
