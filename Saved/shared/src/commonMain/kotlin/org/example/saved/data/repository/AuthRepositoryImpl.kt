package org.example.saved.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import org.example.saved.data.network.model.LoginRequestDto
import org.example.saved.data.network.model.RegisterRequestDto
import org.example.saved.data.network.model.TokenResponseDto
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.repository.TokenStorage

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = client.post("login") {
                setBody(LoginRequestDto(email, password))
            }

            if (response.status.isSuccess()) {
                val tokens = response.body<TokenResponseDto>()
                tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка авторизации. Код: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val response = client.post("register") {
                setBody(RegisterRequestDto(email = email, password = password))
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка сервера: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }

    override suspend fun logout() {
        tokenStorage.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getRefreshToken() != null
    }
}
