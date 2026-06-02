package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Единый формат ошибки бэкенда: {"error": "Описание ошибки"}.
 * Используется в [org.example.saved.data.network.toApiException], чтобы показать
 * пользователю текст ошибки от сервера (например, при 409/400/500).
 */
@Serializable
data class ErrorDto(
    @SerialName("error") val error: String? = null,
)
