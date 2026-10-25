package com.example.medicalrep.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.medicalrep.ui.navigation.model.AuthGraph
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
            // Leaving the auth graph (login success) must not leave Login on the back stack.
            onNavigate = {
                navController.graph.setStartDestination(it.route)
                navController.navigate(it.route) {
                    popUpTo<AuthGraph.Root> { inclusive = true }
                  //  launchSingleTop = true
                }
            },
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
