package com.example.medicalrep.ui.screens.login.main

sealed interface LoginEvents {
    data object SignInClicked : LoginEvents
    data object LanguageClicked : LoginEvents
}
