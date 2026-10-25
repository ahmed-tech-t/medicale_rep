package com.example.medicalrep.ui.screens.root

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicalrep.ui.common.location.LOCATION_PERMISSIONS
import com.example.medicalrep.ui.common.location.LocationStatus
import com.example.medicalrep.ui.common.location.LocationStatusEvents
import com.example.medicalrep.ui.common.location.LocationStatusViewModel
import com.example.medicalrep.ui.common.location.hasLocationPermission
import com.example.medicalrep.ui.common.location.openLocationSettings
import com.example.medicalrep.ui.common.location.rememberLocationPermissionLauncher
import com.example.medicalrep.ui.components.widgets.LocationWarningBanner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.components.ScaffoldComponent
import com.appcore.core.ui.utils.HandleUiEvents
import com.example.medicalrep.ui.navigation.AppNavHost
import com.example.medicalrep.ui.navigation.model.AuthGraph
import com.example.medicalrep.ui.navigation.model.MainGraph

@Composable
fun AppRoot(
    rootViewModel: RootViewModel,
    startDestination: Any,
    locationViewModel: LocationStatusViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Login has a light background (dark icons); the main graph has a brand header (light icons).
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isAuth = backStackEntry?.destination?.hierarchy?.any { it.hasRoute<AuthGraph.Root>() } ?: false
    val isMain = backStackEntry?.destination?.hierarchy?.any { it.hasRoute<MainGraph.Root>() } ?: false
    val view = LocalView.current
    val context = LocalContext.current

    // Location is only needed after login: ask once the main graph opens; the status is re-read on every resume.
    val locationStatus by locationViewModel.status.collectAsStateWithLifecycle()
    val showLocationWarning = isMain && locationStatus != LocationStatus.Ready
    val permissionLauncher = rememberLocationPermissionLauncher()
    LaunchedEffect(isMain) {
        if (isMain && !context.hasLocationPermission()) permissionLauncher.launch(LOCATION_PERMISSIONS)
    }
    LifecycleResumeEffect(Unit) {
        locationViewModel.onEvent(LocationStatusEvents.Refresh)
        onPauseOrDispose {}
    }

    LaunchedEffect(isAuth, showLocationWarning) {
        val window = (view.context as Activity).window
        // The red warning bar needs light icons, whatever screen is shown.
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isAuth && !showLocationWarning
    }

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
        Column {
            if (showLocationWarning) {
                LocationWarningBanner(
                    status = locationStatus,
                    onActionClick = { context.openLocationSettings(locationStatus) },
                )
            }
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                snackbarHostState = snackbarHostState,
                // The banner already sits under the status bar, so screens must not pad for it again.
                modifier = Modifier
                    .weight(1f)
                    .then(if (showLocationWarning) Modifier.consumeWindowInsets(WindowInsets.statusBars) else Modifier),
            )
        }
    }
}
