package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.domain.repo.RouteRepo
import com.example.medicalrep.ui.navigation.model.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitLocationMapViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val routeRepo: RouteRepo,
) : BaseViewModel(appSettingsRepo) {

    private val route = savedStateHandle.toRoute<MainGraph.VisitLocationMap>()

    private val _state = MutableStateFlow(
        VisitLocationMapState(
            visitLocation = MapCameraTarget(route.visitLatitude, route.visitLongitude),
            accountLocation = MapCameraTarget(route.accountLatitude, route.accountLongitude),
            travelSeconds = route.travelSeconds,
        )
    )
    val state: StateFlow<VisitLocationMapState> = _state.asStateFlow()

    init {
        // Within range of the account there is nothing to route.
        if (!_state.value.isAtAccount) viewModelScope.launch { loadRoute() }
    }

    private suspend fun loadRoute() {
        val rep = _state.value.visitLocation
        val account = _state.value.accountLocation
        routeRepo.getRoute(
            listOf(RoutePoint(rep.latitude, rep.longitude), RoutePoint(account.latitude, account.longitude))
        ).collect { result ->
            if (result is Resource.Success) _state.update { it.copy(route = result.data) }
        }
    }

    fun onEvent(event: VisitLocationMapEvents) {
        when (event) {
            VisitLocationMapEvents.Back -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
