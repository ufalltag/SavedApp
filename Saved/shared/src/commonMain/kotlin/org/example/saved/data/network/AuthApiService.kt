package org.example.saved.data.network

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.saved.data.network.model.ChangePasswordRequestDto
import org.example.saved.data.network.model.LoginRequestDto
import org.example.saved.data.network.model.LoginTokenResponseDto
import org.example.saved.data.network.model.ProfileResponseDto
import org.example.saved.data.network.model.RegisterRequestDto

/**
 * Транспортный слой для эндпоинтов авторизации.
 *
 * Отвечает только за HTTP-вызовы login/register и их DTO.
 * Сохранение токенов и пересоздание клиента — забота [AuthRepository].
 */
class AuthApiService(
    private val clientProvider: HttpClientProvider
) {
    private val client get() = clientProvider.client

    suspend fun login(email: String, password: String): Result<LoginTokenResponseDto> =
        safeApiCall { client.post("login") { setBody(LoginRequestDto(email, password)) } }

    suspend fun register(email: String, password: String, username: String): Result<Unit> =
        safeApiCallNoContent {
            client.post("register") {
                setBody(RegisterRequestDto(email = email, password = password, username = username))
            }
        }

    suspend fun getProfile(): Result<ProfileResponseDto> =
        safeApiCall { client.get("profile") }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> =
        safeApiCallNoContent {
            client.put("change-password") {
                setBody(ChangePasswordRequestDto(oldPassword = oldPassword, newPassword = newPassword))
            }
        }
}
