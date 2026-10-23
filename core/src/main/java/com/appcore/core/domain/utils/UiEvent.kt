package com.appcore.core.domain.utils

/**
 * One-time UI effects shared by every app. An interface (not sealed) so each app can add its own
 * feature events, e.g. `sealed class SubscriptionEvent : UiEvent`.
 */
interface UiEvent {
    /** [route] is a type-safe Navigation Compose route (`@Serializable` object/class). */
    data class Navigate(val route: Any) : UiEvent
    data class NavigateGraph(
        val route: Any,
        val startDestination: Any? = null,
    ) : UiEvent

    data object NavigationUp : UiEvent
    data object FinishActivity : UiEvent
    data object HideKeyboard : UiEvent
    data class ShowSnackBar(val text: UiText) : UiEvent
    data class OpenUrl(val url: String) : UiEvent
}
