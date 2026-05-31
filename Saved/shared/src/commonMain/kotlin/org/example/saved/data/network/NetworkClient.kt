package org.example.saved.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.saved.data.network.model.RefreshRequestDto
import org.example.saved.data.network.model.RefreshTokenResponseDto
import org.example.saved.domain.repository.TokenStorage

fun createHttpClient(tokenStorage: TokenStorage): HttpClient {
    return HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000L
            connectTimeoutMillis = 15_000L
            socketTimeoutMillis = 60_000L
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        defaultRequest {
            url("http://localhost:8080/")
            contentType(ContentType.Application.Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val access = tokenStorage.getAccessToken() ?: return@loadTokens null
                    val refresh = tokenStorage.getRefreshToken() ?: return@loadTokens null
                    BearerTokens(access, refresh)
                }

                refreshTokens {
                    val oldRefreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null

                    try {
                        val response = client.post("http://localhost:8080/refresh") {
                            setBody(RefreshRequestDto(refreshToken = oldRefreshToken))
                        }

                        if (!response.status.isSuccess()) {
                            tokenStorage.clearTokens()
                            return@refreshTokens null
                        }

                        val refreshed = response.body<RefreshTokenResponseDto>()
                        tokenStorage.saveTokens(
                            accessToken = refreshed.accessToken,
                            refreshToken = oldRefreshToken
                        )

                        BearerTokens(
                            accessToken = refreshed.accessToken,
                            refreshToken = oldRefreshToken
                        )
                    } catch (e: Exception) {
                        tokenStorage.clearTokens()
                        null
                    }
                }

                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    !path.contains("login") && !path.contains("register")
                }
            }
        }
    }
}
