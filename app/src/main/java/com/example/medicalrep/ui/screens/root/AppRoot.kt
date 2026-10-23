package com.example.medicalrep.ui.screens.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.components.ScaffoldComponent
import com.appcore.core.ui.utils.HandleUiEvents
import com.example.medicalrep.ui.navigation.AppNavHost

@Composable
fun AppRoot(
    rootViewModel: RootViewModel,
    startDestination: Any,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    rootViewModel.uiEvent.HandleUiEvents(
        snackbarHostState = snackbarHostState,
        onNavigate = { navController.navigate(it.route) },
        navigationUp = { navController.navigateUp() },
        onCustomEvent = { event ->
            if (event is UiEvent.NavigateGraph) {
                // Switch graphs and drop everything behind (e.g. session expired -> Login).
                navController.navigate(event.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
    )

    ScaffoldComponent(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHostState = snackbarHostState
    ) { padding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            snackbarHostState = snackbarHostState,
          //  modifier = Modifier.padding(padding),
        )
    }
}
