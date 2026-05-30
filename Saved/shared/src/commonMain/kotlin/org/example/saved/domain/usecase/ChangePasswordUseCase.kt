package org.example.saved.domain.usecase

import org.example.saved.domain.repository.AuthRepository

/**
 * Смена пароля текущего пользователя (PUT /change-password).
 * Требует верный текущий пароль; новый должен отличаться от старого
 * и проходить проверку сложности на бэкенде (он и вернёт текст ошибки).
 */
class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): Result<Unit> =
        authRepository.changePassword(oldPassword, newPassword)
}
