package org.example.saved.domain.usecase

import org.example.saved.domain.repository.AuthRepository

class IsLoggedInUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean = authRepository.isLoggedIn()
}
