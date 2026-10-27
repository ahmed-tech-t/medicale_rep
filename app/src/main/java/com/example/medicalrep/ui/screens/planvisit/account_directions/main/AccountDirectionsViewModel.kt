package com.example.medicalrep.ui.screens.planvisit.account_directions.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.GeocodingRepo
import com.example.medicalrep.domain.repo.RouteRepo
import com.appcore.core.data.local.session.AuthSessionManager
import com.example.medicalrep.domain.models.lastLocation
import com.example.medicalrep.ui.navigation.model.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDirectionsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val session: AuthSessionManager,
    private val routeRepo: RouteRepo,
    private val geocodingRepo: GeocodingRepo,
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
        loadLocation()
    }

    /** Reads the rep's position, then its street and (when he is not at the account yet) the road route. */
    private fun loadLocation() {
        if (_state.value.isLocating) return
        _state.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            val user = session.user.value.lastLocation
            if (user == null) {
                _state.update { it.copy(isLocating = false) }
                return@launch
            }
            _state.update {
                it.copy(
                    userLocation = user,
                    capturedAtMillis = System.currentTimeMillis(),
                    route = null,
                    userStreet = null,
                    isLocating = false,
                )
            }
            launch { loadStreet(user) }
            // Within range of the account there is nothing to navigate, so no route.
            if (!_state.value.isAtAccount) loadRoute(user)
        }
    }

    private suspend fun loadStreet(user: GeoPoint) {
        geocodingRepo.getAddress(GeoPoint(user.latitude, user.longitude)).collect { result ->
            if (result is Resource.Success) _state.update { it.copy(userStreet = result.data) }
        }
    }

    private suspend fun loadRoute(user: GeoPoint) {
        val account = _state.value.accountLocation
        routeRepo.getRoute(
            listOf(GeoPoint(user.latitude, user.longitude), GeoPoint(account.latitude, account.longitude))
        ).collect { result ->
            if (result is Resource.Success) _state.update { it.copy(route = result.data) }
        }
    }

    fun onEvent(event: AccountDirectionsEvents) {
        when (event) {
            AccountDirectionsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            AccountDirectionsEvents.ToggleCard -> _state.update { it.copy(isCardExpanded = !it.isCardExpanded) }
            AccountDirectionsEvents.RefreshLocation -> loadLocation()
        }
    }
}
