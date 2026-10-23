package com.example.medicalrep.ui.navigation.nav_graph.main

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.home.main.HomeScreen

fun NavGraphBuilder.mainNavGraph(
    onNavigate: (UiEvent.Navigate) -> Unit,
    navigationUp: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    navigation<MainGraph.Root>(startDestination = MainGraph.Home) {
        composable<MainGraph.Home> {
            HomeScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
