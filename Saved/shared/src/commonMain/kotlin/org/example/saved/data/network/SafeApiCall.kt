package org.example.saved.data.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.example.saved.data.network.model.ErrorDto

/**
 * Ошибка HTTP-уровня: сервер ответил, но со статусом != 2xx.
 *
 * Несёт [code] (чтобы вызывающий слой мог отличать 401/404/409/500) и
 * человекочитаемое [message]: сначала пытаемся показать текст из тела ответа
 * ({"error": "Папка с таким именем уже существует"}), а если его нет —
 * подставляем дефолтное сообщение по коду.
 */
class ApiException(
    val code: Int,
    serverMessage: String? = null,
) : Exception(serverMessage ?: defaultMessageForCode(code))

private val errorJson =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

/**
 * Достаёт текст ошибки из тела ответа ({"error": "..."}).
 * Если тело пустое или не JSON — возвращает [ApiException] с дефолтным текстом по коду.
 */
suspend fun HttpResponse.toApiException(): ApiException {
    val serverMessage =
        try {
            val text = bodyAsText()
            if (text.isBlank()) {
                null
            } else {
                errorJson.decodeFromString<ErrorDto>(text).error?.takeIf { it.isNotBlank() }
            }
        } catch (e: Exception) {
            null
        }
    return ApiException(status.value, serverMessage)
}

private fun defaultMessageForCode(code: Int): String =
    when (code) {
        400 -> "Bad request"
        401 -> "Unauthorized"
        403 -> "Access denied"
        404 -> "Not found"
        409 -> "This record already exists"
        500 -> "Server error"
        else -> "Network error (HTTP $code)"
    }

/**
 * Оборачивает сетевой вызов, у которого есть тело ответа.
 *
 * - Парсит тело в [T] при успехе (2xx).
 * - Возвращает [ApiException] (с сообщением сервера) при не-2xx.
 * - Ловит любые сетевые/сериализационные исключения в [Result.failure].
 *
 * inline + reified нужны, чтобы [HttpResponse.body] знал тип [T] на месте вызова.
 */
suspend inline fun <reified T> safeApiCall(request: () -> HttpResponse): Result<T> =
    try {
        val response = request()
        if (response.status.isSuccess()) {
            Result.success(response.body())
        } else {
            Result.failure(response.toApiException())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

/**
 * Вариант для вызовов без тела ответа (DELETE, PUT-обновления и т.п.).
 * Проверяет только статус и возвращает [Unit].
 */
suspend inline fun safeApiCallNoContent(request: () -> HttpResponse): Result<Unit> =
    try {
        val response = request()
        if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            Result.failure(response.toApiException())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
