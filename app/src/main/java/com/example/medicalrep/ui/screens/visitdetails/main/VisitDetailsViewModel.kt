package com.example.medicalrep.ui.screens.visitdetails.main

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.visitdetails.utils.VisitDetailsFakes
import com.example.medicalrep.ui.screens.visitdetails.utils.hasLocations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VisitDetailsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(appSettingsRepo) {

    private val visitId: String = savedStateHandle.toRoute<MainGraph.VisitDetails>().visitId

    // TODO: load the visit with [visitId] from Room.
    private val _state = MutableStateFlow(VisitDetailsState(visit = VisitDetailsFakes.visit(visitId)))
    val state: StateFlow<VisitDetailsState> = _state.asStateFlow()

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
