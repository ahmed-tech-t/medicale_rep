package com.example.medicalrep.ui.screens.visitdetails.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.visitdetails.utils.hasLocations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.domain.repo.VisitRepo
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VisitDetailsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    visitRepo: VisitRepo,
) : BaseViewModel(appSettingsRepo) {

    // The route's own argument key: toRoute needs a real Bundle, which plain JVM tests do not have.
    private val visitId: String = savedStateHandle.get<String>("visitId").orEmpty()

    private val _state = MutableStateFlow(VisitDetailsState(visit = VisitUi(id = visitId)))
    val state: StateFlow<VisitDetailsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            visitRepo.get(visitId)?.let { visit -> _state.update { it.copy(visit = visit.toUi()) } }
        }
    }

    fun onEvent(event: VisitDetailsEvents) {
        when (event) {
            VisitDetailsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            VisitDetailsEvents.ViewOnMap -> {
                val visit = _state.value.visit
                if (visit.hasLocations) {
                    sendEvent(
                        UiEvent.Navigate(
                            MainGraph.VisitLocationMap(
                                visitLatitude = visit.visitLatitude,
                                visitLongitude = visit.visitLongitude,
                                accountLatitude = visit.accountLatitude,
                                accountLongitude = visit.accountLongitude,
                                travelSeconds = visit.travelSeconds,
                            )
                        )
                    )
                } else {
                    sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.visit_location_unavailable)))
                }
            }
        }
    }
}
