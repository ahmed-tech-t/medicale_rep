package com.example.medicalrep.ui.screens.planvisit.account_directions.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.RouteRepo
import com.appcore.core.data.local.session.AuthSessionManager
import com.example.medicalrep.domain.models.lastLocation
import com.example.medicalrep.ui.navigation.model.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDirectionsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val session: AuthSessionManager,
    private val routeRepo: RouteRepo,
) : BaseViewModel(appSettingsRepo) {

    private val route = savedStateHandle.toRoute<MainGraph.AccountDirections>()

    private val _state = MutableStateFlow(
        AccountDirectionsState(
            accountLocation = GeoPoint(route.accountLatitude, route.accountLongitude),
            accountName = route.accountName,
        )
    )
    val state: StateFlow<AccountDirectionsState> = _state.asStateFlow()

    init {
        // The map follows the session: every new GPS fix moves the rep and reloads the street and the route.
        viewModelScope.launch {
            session.user.map { it.lastLocation }.filterNotNull().distinctUntilChanged().collectLatest(::showLocation)
        }
        viewModelScope.launch {
            session.user.map { it.lastAddress }.distinctUntilChanged().collect { street ->
                _state.update { it.copy(userStreet = street) }
            }
        }
    }

    /** Shows the rep at [user], then (when he is not at the account yet) the road route. */
    private suspend fun showLocation(user: GeoPoint) {
        _state.update {
            it.copy(
                userLocation = user,
                capturedAtMillis = session.user.value.lastLocationTimeMillis,
                route = null,
            )
        }
        // Within range of the account there is nothing to navigate, so no route.
        if (!_state.value.isAtAccount) loadRoute(user)
    }

    private suspend fun loadRoute(user: GeoPoint) {
        val account = _state.value.accountLocation
        routeRepo.getRoute(listOf(user, account)).collect { result ->
            if (result is Resource.Success) _state.update { it.copy(route = result.data) }
        }
    }

    fun onEvent(event: AccountDirectionsEvents) {
        when (event) {
            AccountDirectionsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            AccountDirectionsEvents.ToggleCard -> _state.update { it.copy(isCardExpanded = !it.isCardExpanded) }
        }
    }
}
