package com.example.medicalrep.ui.screens.officework.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.RepRequest
import com.example.medicalrep.domain.models.RequestCategory
import com.example.medicalrep.domain.repo.RequestRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OfficeWorkViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val requestRepo: RequestRepo,
    private val session: AuthSessionManager,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(OfficeWorkState())
    val state: StateFlow<OfficeWorkState> = _state.asStateFlow()

    fun onEvent(event: OfficeWorkEvents) {
        when (event) {
            OfficeWorkEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is OfficeWorkEvents.OnDateChange -> _state.update { it.copy(dateMillis = event.millis) }
            OfficeWorkEvents.ToggleWorkTypeMenu -> _state.update { it.copy(showWorkTypeMenu = !it.showWorkTypeMenu) }
            is OfficeWorkEvents.OnWorkTypeSelect -> _state.update { it.copy(workType = event.type, showWorkTypeMenu = false) }
            is OfficeWorkEvents.OnShiftSelect -> _state.update { it.copy(shift = event.shift) }
            OfficeWorkEvents.Submit -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        val repId = session.user.value.id?.toString() ?: return
        viewModelScope.launch {
            requestRepo.save(
                RepRequest(
                    id = UUID.randomUUID().toString(),
                    repId = repId,
                    dateMillis = s.dateMillis,
                    category = RequestCategory.OfficeWork,
                    subType = s.workType::class.simpleName.orEmpty(),
                    shift = s.shift,
                    notes = s.notes.text.toString(),
                    createdAtMillis = System.currentTimeMillis(),
                )
            )
            sendEvent(UiEvent.NavigationUp)
        }
    }
}
