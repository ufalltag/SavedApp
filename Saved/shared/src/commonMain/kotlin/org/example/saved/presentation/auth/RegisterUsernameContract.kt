package org.example.saved.presentation.auth

/**
 * Состояние 2-го шага регистрации: ввод username.
 * email/пароль приходят в конструктор ViewModel с предыдущего экрана,
 * поэтому в состоянии хранится только username и флаг загрузки.
 */
data class RegisterUsernameState(
    val username: String = "",
    val isLoading: Boolean = false
)

sealed interface RegisterUsernameSideEffect {
    // Показать ошибку (валидация или ответ сервера)
    data class ShowError(val message: String) : RegisterUsernameSideEffect

    // Регистрация успешна — вернуться на экран входа.
    data class NavigateToLogin(val message: String) : RegisterUsernameSideEffect
}
