package org.example.saved.domain.usecase

import org.example.saved.domain.model.UserProfile
import org.example.saved.domain.repository.AuthRepository

class GetProfileUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<UserProfile> = authRepository.getProfile()
}
