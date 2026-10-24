package com.example.medicalrep.ui.screens.sampleandgifts.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.screens.sampleandgifts.utils.SampleAndGiftsFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SampleAndGiftsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: replace the sample data with the rep's custody from Room.
    private val _state = MutableStateFlow(SampleAndGiftsFakes.state())
    val state: StateFlow<SampleAndGiftsState> = _state.asStateFlow()

    fun onEvent(event: SampleAndGiftsEvents) {
        when (event) {
            is SampleAndGiftsEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
        }
    }
}
