package com.appcore.core.domain.models

/** Startup result; each app maps it to its own start destination. */
data class AppBootstrap(
    val authStatus: UserAuthStatus,
    val showOnBoarding: Boolean,
    val language: String,
)
