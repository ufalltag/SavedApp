package org.example.saved.data.repository

import org.example.saved.data.network.AuthApiService
import org.example.saved.data.network.HttpClientProvider
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.repository.TokenStorage

/**
 * Логика авторизации: HTTP делегирован [AuthApiService], а репозиторий
 * отвечает за побочные эффекты входа/выхода — сохранение/очистку токенов
 * и пересоздание HTTP-клиента, чтобы Ktor сбросил кэш bearer-токена.
 */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage,
    private val clientProvider: HttpClientProvider
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> =
        api.login(email, password).map { tokens ->
            tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
            clientProvider.recreate()
        }

    override suspend fun register(email: String, password: String, username: String): Result<Unit> =
        api.register(email, password, username)

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> =
        api.changePassword(oldPassword, newPassword)

    override suspend fun logout() {
        tokenStorage.clearTokens()
        clientProvider.recreate()
    }

    override suspend fun isLoggedIn(): Boolean =
        tokenStorage.getRefreshToken() != null
}
