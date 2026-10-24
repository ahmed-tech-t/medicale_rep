package com.example.medicalrep.ui.screens.datacenter.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.screens.datacenter.utils.DataCenterFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DataCenterViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(DataCenterFakes.state())
    val state: StateFlow<DataCenterState> = _state.asStateFlow()

    fun onEvent(event: DataCenterEvents) {
        when (event) {
            DataCenterEvents.OnBack -> sendEvent(UiEvent.NavigationUp)
            is DataCenterEvents.OnSync -> syncCategory(event.categoryId)
        }
    }

    // TODO: call the server to sync the category and flip its status to Syncing while it runs.
    private fun syncCategory(categoryId: String) = Unit
}
