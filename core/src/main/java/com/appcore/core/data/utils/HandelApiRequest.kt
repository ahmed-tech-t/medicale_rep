package com.appcore.core.data.utils

import android.util.Log
import com.appcore.core.data.remote.dto.BaseResponse
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

fun Throwable.toApiErrorMessage(json: Json): String = when (this) {
    is CancellationException -> throw this
    is UnknownHostException -> "Cannot connect to server. Check your internet."
    is ConnectException -> "Network unreachable. Please try again."
    is SocketException -> "Connection lost. Please check your network."
    is IOException -> {
        Log.e("apiRequest", "IOException $message")
        message ?: "Network error. Please try again."
    }

    is HttpException -> {
        val errorBody = runCatching { response()?.errorBody()?.string() }.getOrNull()
        parseHttpErrorMessage(errorBody, json)
    }

    else -> {
        Log.e("apiRequest", "Exception $message")
        message ?: "An unexpected error occurred"
    }
}

inline fun <T : Any, R> apiRequest(
    json: Json,
    crossinline apiCall: suspend () -> BaseResponse<T>,
    crossinline onSuccess: (T?) -> R,
): Flow<Resource<R>> {
    return flow<Resource<R>> {
        val response = apiCall()

        if (response.status) {
            emit(Resource.Success(onSuccess(response.data), message = response.message))
        } else {
            emit(Resource.Error(response.message))
        }
    }.onStart {
        emit(Resource.Loading())
    }.catch { e ->
        emit(Resource.Error(e.toApiErrorMessage(json)))
    }.flowOn(Dispatchers.IO)
}


suspend fun <T> handleApiResponse(
    flow: Flow<Resource<T>>,
    isLoading: (Boolean) -> Unit = {},
    isStreaming: (Boolean) -> Unit = {},
    result: suspend (T?, String) -> Unit,
    streaming: (suspend (T?) -> Unit)? = null,
    retryIfFail: Boolean = false,
    sendEvent: suspend (UiEvent, String, Int) -> Unit,
    maxRetries: Int = 3
) {
    var retriesLeft = maxRetries
    var looping = true

    while (looping) {
        flow.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    sendEvent(
                        UiEvent.ShowSnackBar(UiText.DynamicText(resource.message.toString())),
                        resource.message.toString(),
                        resource.code ?: 0
                    )
                    if (retryIfFail && retriesLeft > 0) {
                        isLoading(true)
                        delay(5000.milliseconds)
                        retriesLeft--
                    } else {
                        looping = false
                        isLoading(false)
                    }
                }

                is Resource.Loading -> {
                    isLoading(true)
                    isStreaming(false)
                }

                is Resource.Success -> {
                    result(resource.data, resource.message ?: "")
                    isLoading(false)
                    isStreaming(false)
                    looping = false
                }

                is Resource.Streaming -> {
                    isStreaming(true)
                    streaming?.invoke(resource.data)
                }
            }
        }
    }
}
