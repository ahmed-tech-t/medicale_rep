package com.appcore.core.ui.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.appcore.core.domain.utils.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <T : UiEvent> Flow<T>.HandleUiEvents(
    snackbarHostState: SnackbarHostState,
    onNavigate: (UiEvent.Navigate) -> Unit,
    navigationUp: () -> Unit,
    finishActivity: () -> Unit = {},
    onCustomEvent: (T) -> Unit = {},
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = this) {
        this@HandleUiEvents.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                UiEvent.NavigationUp -> navigationUp()
                is UiEvent.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = event.text.asString(context))
                    }
                }
                UiEvent.HideKeyboard -> {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
                UiEvent.FinishActivity -> finishActivity()
                else -> onCustomEvent(event)
            }
        }
    }
}
