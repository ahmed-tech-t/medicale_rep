package com.example.medicalrep.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.medicalrep.ui.navigation.nav_graph.authNavGraph
import com.example.medicalrep.ui.navigation.nav_graph.main.mainNavGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        authNavGraph(
            onNavigate = { navController.navigate(it.route) },
            navigationUp = { navController.navigateUp() },
            snackbarHostState = snackbarHostState,
        )
        mainNavGraph(
            onNavigate = { navController.navigate(it.route) },
            navigationUp = { navController.navigateUp() },
            snackbarHostState = snackbarHostState,
        )
    }
}
