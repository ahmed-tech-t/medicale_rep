package com.example.medicalrep.ui.screens.notifications.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.NotificationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val notificationRepo: NotificationRepo,
    private val session: AuthSessionManager,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            session.user
                .map { it.id?.toString() }
                .distinctUntilChanged()
                .flatMapLatest { repId ->
                    if (repId == null) flowOf(emptyList()) else notificationRepo.notificationsOf(repId)
                }
                .collect { notifications -> _state.update { it.copy(notifications = notifications) } }
        }
    }

    fun onEvent(event: NotificationEvents) {
        when (event) {
            NotificationEvents.OnBack -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
