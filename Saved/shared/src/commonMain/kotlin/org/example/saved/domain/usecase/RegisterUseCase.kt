package org.example.saved.domain.usecase

import org.example.saved.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, username: String): Result<Unit> =
        authRepository.register(email, password, username)
}
