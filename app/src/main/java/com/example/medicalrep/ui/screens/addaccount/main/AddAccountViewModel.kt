package com.example.medicalrep.ui.screens.addaccount.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.screens.addaccount.utils.AddAccountFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load the options from Room / the backend and start the map at the device's real location.
    private val _state = MutableStateFlow(AddAccountFakes.state())
    val state: StateFlow<AddAccountState> = _state.asStateFlow()

    fun onEvent(event: AddAccountEvents) {
        when (event) {
            AddAccountEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is AddAccountEvents.SelectCity -> _state.update { it.copy(city = event.value) }
            is AddAccountEvents.SelectDistrict -> _state.update { it.copy(district = event.value) }
            is AddAccountEvents.SelectEntityType -> _state.update { it.copy(entityType = event.value) }
            is AddAccountEvents.SelectClass -> _state.update { it.copy(classCategory = event.value) }
            is AddAccountEvents.SelectSpecialty -> _state.update { it.copy(specialty = event.value) }
            is AddAccountEvents.PinPlaced -> _state.update {
                it.copy(pin = MapCameraTarget(event.latitude, event.longitude))
            }
            is AddAccountEvents.SignPhotoCaptured -> _state.update {
                it.copy(signPhoto = event.document.uri, capturedAtMillis = System.currentTimeMillis())
            }
            AddAccountEvents.Save -> save()
        }
    }

    // TODO: persist the account (Room / backend) and watermark the sign photo with the pin location.
    private fun save() {
        if (!_state.value.canSave) return
        sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.add_account_saved)))
        sendEvent(UiEvent.NavigationUp)
    }
}
