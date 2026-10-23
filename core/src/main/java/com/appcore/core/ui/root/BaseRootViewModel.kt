package com.appcore.core.ui.root

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.models.AppBootstrap
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.usecases.bootstrap.BootstrapUseCase
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "BaseRootViewModel"

/**
 * Bootstraps the app (splash → start destination) and reacts to session expiry.
 * Apps extend it as their `@HiltViewModel` root ViewModel and map [AppBootstrap] to their start route.
 */
abstract class BaseRootViewModel(
    private val bootstrapUseCase: BootstrapUseCase,
    private val authSessionManager: AuthSessionManager,
    private val appSettingsManager: AppSettingsManager,
) : ViewModel() {

    private var bootstrapStarted = false
    private val _state = MutableStateFlow(RootState())

    // Started on first collection (not in init) so subclass overrides never run before the subclass is built.
    val state: StateFlow<RootState> = _state
        .onStart {
            if (!bootstrapStarted) {
                bootstrapStarted = true
                bootstrap()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RootState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private var sessionObserverJob: Job? = null

    fun onEvent(event: RootEvents) {
        when (event) {
            RootEvents.RetryBootstrap -> bootstrap()
        }
    }

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    /**
     * Runs while the splash is still visible, for data the first screen needs (e.g. geo detection).
     * The splash waits for it, so wrap network calls in `withTimeoutOrNull`. A failure never blocks startup.
     */
    protected open suspend fun loadDuringSplash(data: AppBootstrap) = Unit

    /** After the splash is hidden: refresh the user, start app-level observers, etc. */
    protected open fun onBootstrapped(data: AppBootstrap) = Unit

    /** The session ended (refresh failed or account blocked): typically navigate to sign-in. */
    protected abstract suspend fun onSessionExpired(message: String?)

    fun fallbackBootstrap() = AppBootstrap(
        authStatus = UserAuthStatus.NotAuthenticated,
        showOnBoarding = true,
        language = appSettingsManager.getLanguageSync(),
    )

    private fun bootstrap() {
        viewModelScope.launch {
            _state.update { it.copy(bootstrap = Resource.Loading()) }

            runCatching { bootstrapUseCase() }
                .onSuccess { data ->
                    observeSession()
                    loadDuringSplashSafely(data)
                    _state.update { it.copy(bootstrap = Resource.Success(data)) }
                    onBootstrapped(data)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            bootstrap = Resource.Error(
                                message = throwable.message ?: "Bootstrap failed",
                                data = fallbackBootstrap(),
                            )
                        )
                    }
                }
        }
    }

    private suspend fun loadDuringSplashSafely(data: AppBootstrap) {
        try {
            loadDuringSplash(data)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "loadDuringSplash failed", e)
        }
    }

    private fun observeSession() {
        sessionObserverJob?.cancel()
        sessionObserverJob = viewModelScope.launch {
            authSessionManager.sessionExpired.collect { message -> onSessionExpired(message) }
        }
    }
}
