package com.example.medicalrep.ui.navigation.nav_graph.main

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.datacenter.main.DataCenterScreen
import com.example.medicalrep.ui.screens.home.main.HomeScreen
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitScreen
import com.example.medicalrep.ui.screens.products.main.ProductsScreen
import com.example.medicalrep.ui.screens.reptracking.main.RepTrackingScreen
import com.example.medicalrep.ui.screens.sampleandgifts.main.SampleAndGiftsScreen
import com.example.medicalrep.ui.screens.visitdetails.main.VisitDetailsScreen
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogScreen
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningScreen

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
        composable<MainGraph.PlanVisit> {
            PlanVisitScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.SampleAndGifts> {
            SampleAndGiftsScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.Products> {
            ProductsScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.VisitLog> {
            VisitLogScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.VisitPlanning> {
            VisitPlanningScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.VisitDetails> {
            VisitDetailsScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.RepTracking> {
            RepTrackingScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
        composable<MainGraph.DataCenter> {
            DataCenterScreen(
                onNavigate = onNavigate,
                navigationUp = navigationUp,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
