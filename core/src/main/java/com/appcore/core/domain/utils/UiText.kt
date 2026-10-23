package com.appcore.core.domain.utils

import android.content.Context

import kotlinx.serialization.Serializable

@Serializable
sealed class UiText {
    @Serializable
    data class DynamicText(val text: String) : UiText()
    @Serializable
    data class StringResource(val resId: Int) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicText -> text
            is StringResource -> context.getString(resId)
        }
    }
}
