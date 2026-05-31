package org.example.saved.domain.repository

import org.example.saved.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, username: String): Result<Unit>
    suspend fun getProfile(): Result<UserProfile>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
