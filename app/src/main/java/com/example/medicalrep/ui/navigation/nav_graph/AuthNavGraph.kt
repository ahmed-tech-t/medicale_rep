package com.example.medicalrep.ui.navigation.nav_graph

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.navigation.model.AuthGraph
import com.example.medicalrep.ui.screens.login.main.LoginScreen

fun NavGraphBuilder.authNavGraph(
    onNavigate: (UiEvent.Navigate) -> Unit,
    navigationUp: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    navigation<AuthGraph.Root>(startDestination = AuthGraph.Login) {
        composable<AuthGraph.Login> {
            LoginScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
