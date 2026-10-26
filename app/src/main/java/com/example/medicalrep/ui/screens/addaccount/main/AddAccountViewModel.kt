package com.example.medicalrep.ui.screens.addaccount.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.ui.common.location.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val accountRepo: AccountRepo,
    locationHelper: LocationHelper,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(AddAccountState())
    val state: StateFlow<AddAccountState> = _state.asStateFlow()

    init {
        collectInto(accountRepo.cities()) { state, cities -> state.copy(cityOptions = cities) }
        collectInto(accountRepo.districts()) { state, districts -> state.copy(districtOptions = districts) }
        collectInto(accountRepo.entityTypes()) { state, types -> state.copy(entityTypeOptions = types) }
        collectInto(accountRepo.specialties()) { state, specialties -> state.copy(specialtyOptions = specialties) }
        viewModelScope.launch {
            locationHelper.getLocation()?.let { location -> _state.update { it.copy(userLocation = location) } }
        }
    }

    private fun <T> collectInto(flow: Flow<T>, transform: (AddAccountState, T) -> AddAccountState) {
        viewModelScope.launch { flow.collect { value -> _state.update { transform(it, value) } } }
    }

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

    /** Stores the account on the phone, not uploaded yet. TODO: watermark the sign photo with the pin location. */
    private fun save() {
        val form = _state.value
        val pin = form.pin
        if (!form.canSave || pin == null) return
        viewModelScope.launch {
            accountRepo.save(
                Account(
                    id = UUID.randomUUID().toString(),
                    name = form.name.text.toString().trim(),
                    entityType = form.entityType,
                    classCategory = form.classCategory,
                    specialty = form.specialty,
                    city = form.city,
                    district = form.district,
                    latitude = pin.latitude,
                    longitude = pin.longitude,
                    syncStatus = SyncStatus.NotSynced,
                    signPhoto = form.signPhoto?.toString(),
                    createdAt = System.currentTimeMillis(),
                ),
            )
            sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.add_account_saved)))
            sendEvent(UiEvent.NavigationUp)
        }
    }
}
