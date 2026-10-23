package com.example.medicalrep.ui.navigation.model

import kotlinx.serialization.Serializable

/** Auth graph: [Root] is the graph itself, the other members are its screens. */
@Serializable
sealed interface AuthGraph {
    @Serializable
    data object Root : AuthGraph

    @Serializable
    data object Login : AuthGraph
}
