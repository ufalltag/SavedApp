package org.example.saved.presentation.auth

/**
 * Состояние 1-го шага регистрации: ввод email и пароля.
 * Здесь нет сетевых запросов — только сбор и базовая валидация полей.
 */
data class RegisterCredentialsState(
    val email: String = "",
    val password: String = "",
)

sealed interface RegisterCredentialsSideEffect {
    // Ошибка валидации (пустые поля и т.п.)
    data class ShowError(
        val message: String,
    ) : RegisterCredentialsSideEffect

    // Перейти на 2-й шаг (ввод username), передавая уже собранные данные.
    data class NavigateToUsername(
        val email: String,
        val password: String,
    ) : RegisterCredentialsSideEffect
}
