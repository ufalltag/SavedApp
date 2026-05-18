package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("name") val name: String? = null
)

@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class RefreshRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)