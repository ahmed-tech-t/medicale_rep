package com.example.medicalrep.ui.screens.login.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class LoginState(
    val showErrors: Boolean = false,
    val language: String = "ar",
    val companyCode: TextFieldState = TextFieldState(),
    val username: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState()
) {
    val otherLanguageCode: String get() = if (language == "ar") "EN" else "AR"
    val companyCodeBlank: Boolean get() = companyCode.text.isBlank()
    val usernameBlank: Boolean get() = username.text.isBlank()
    val passwordBlank: Boolean get() = password.text.isBlank()

    val buttonEnabled: Boolean get() = !companyCodeBlank && !usernameBlank && !passwordBlank
}


