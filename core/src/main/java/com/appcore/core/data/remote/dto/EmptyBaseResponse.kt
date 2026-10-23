package com.appcore.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmptyBaseResponse(
    @SerialName("status")
    val status: Boolean,
    @SerialName("message")
    val message: String,
)
