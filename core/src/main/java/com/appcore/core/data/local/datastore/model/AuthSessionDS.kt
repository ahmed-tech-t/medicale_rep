package com.appcore.core.data.local.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthSessionDS(
    val accessToken: String = "",
    val refreshToken: String = "",
)
