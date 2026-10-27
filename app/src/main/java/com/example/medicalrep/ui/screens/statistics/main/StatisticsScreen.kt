package com.example.medicalrep.ui.screens.statistics.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.HandleUiEvents
import com.example.medicalrep.ui.screens.statistics.components.StatisticsUi

@Composable
fun StatisticsScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    navigationUp: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    viewModel.uiEvent.HandleUiEvents(
        snackbarHostState = snackbarHostState,
        onNavigate = onNavigate,
        navigationUp = navigationUp,
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    StatisticsUi(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
