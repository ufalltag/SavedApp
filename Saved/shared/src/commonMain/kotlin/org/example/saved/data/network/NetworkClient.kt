package org.example.saved.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.saved.data.network.model.RefreshRequestDto
import org.example.saved.data.network.model.TokenResponseDto
import org.example.saved.domain.repository.TokenStorage

fun createHttpClient(tokenStorage: TokenStorage): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            url("http://192.168.31.134:8080/")
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
                    val oldRefreshToken =
                        tokenStorage.getRefreshToken() ?: return@refreshTokens null

                    try {
                        val response = client.post("http://192.168.31.134:8080/refresh") {
                            setBody(RefreshRequestDto(refreshToken = oldRefreshToken))
                        }

                        if (!response.status.isSuccess()) {
                            tokenStorage.clearTokens()
                            return@refreshTokens null
                        }

                        val newTokens = response.body<TokenResponseDto>()
                        tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)

                        return@refreshTokens BearerTokens(
                            newTokens.accessToken,
                            newTokens.refreshToken
                        )
                    } catch (e: Exception) {
                        tokenStorage.clearTokens()
                        return@refreshTokens null
                    }
                }

                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    !path.contains("/login") || !path.contains("/register")
                }
            }
        }
    }
}
