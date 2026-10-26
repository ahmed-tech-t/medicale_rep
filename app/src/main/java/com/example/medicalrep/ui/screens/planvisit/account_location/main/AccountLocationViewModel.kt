package com.example.medicalrep.ui.screens.planvisit.account_location.main

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.planvisit.account_location.utils.directionsUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountLocationViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(appSettingsRepo) {

    private val route = savedStateHandle.toRoute<MainGraph.AccountLocation>()

    private val _state = MutableStateFlow(
        AccountLocationState(
            accountLocation = GeoPoint(route.accountLatitude, route.accountLongitude),
            accountName = route.accountName,
            address = route.address,
            classCategory = route.classCategory,
            accountType = route.accountType,
            specialty = route.specialty,
        )
    )
    val state: StateFlow<AccountLocationState> = _state.asStateFlow()

    fun onEvent(event: AccountLocationEvents) {
        when (event) {
            AccountLocationEvents.Back -> sendEvent(UiEvent.NavigationUp)
            AccountLocationEvents.GetDirections -> {
                val account = _state.value.accountLocation
                sendEvent(UiEvent.OpenUrl(directionsUrl(account.latitude, account.longitude)))
            }
        }
    }
}
