package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("username")
    val username: String
)

@Serializable
data class LoginTokenResponseDto(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponseDto(
    @SerialName("accessToken")
    val accessToken: String
)

@Serializable
data class RefreshRequestDto(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("old_password")
    val oldPassword: String,
    @SerialName("new_password")
    val newPassword: String
)

@Serializable
data class ProfileResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("username")
    val username: String
)