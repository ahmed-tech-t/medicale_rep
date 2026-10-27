package com.example.medicalrep.ui.screens.statistics.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.StatisticsRepo
import com.example.medicalrep.ui.utils.startOfNextWeek
import com.example.medicalrep.ui.utils.startOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    statisticsRepo: StatisticsRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        // This week (Saturday to Friday) is fixed when the screen opens; the numbers follow the data while it is open.
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            statisticsRepo.between(startOfWeek(now), startOfNextWeek(now)).collect { _state.value = StatisticsState(it) }
        }
    }

    fun onEvent(event: StatisticsEvents) {
        when (event) {
            StatisticsEvents.Back -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
