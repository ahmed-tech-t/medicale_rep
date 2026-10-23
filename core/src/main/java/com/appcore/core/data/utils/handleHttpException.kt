package com.appcore.core.data.utils


import com.appcore.core.data.remote.dto.EmptyBaseResponse

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


fun parseHttpErrorMessage(errorBody: String?, json: Json): String {
    if (errorBody.isNullOrBlank()) return "An unexpected error occurred"
    return try {
        json.decodeFromString<EmptyBaseResponse>(errorBody).message
            .takeIf { it.isNotBlank() } ?: "An unexpected error occurred"
    } catch (_: SerializationException) {
        errorBody // server returned plain text instead of JSON
    }
}
