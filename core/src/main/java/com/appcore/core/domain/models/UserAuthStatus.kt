package com.appcore.core.domain.models

sealed interface UserAuthStatus {
    object Authenticated : UserAuthStatus
    object NotAuthenticated : UserAuthStatus
}
