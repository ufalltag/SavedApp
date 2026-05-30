package org.example.saved.presentation.auth

/**
 * Состояние экрана Входа.
 * UI просто читает поля и рисует по ним интерфейс.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false      // Показываем крутилку (ProgressView)
)

/**
 * Разовые события экрана Входа (fire-and-forget): навигация и алерты.
 * Не сохраняйте их в @State, иначе алерт всплывёт повторно при каждой перерисовке.
 */
sealed interface LoginSideEffect {
    // Показать ошибку (красный алерт)
    data class ShowError(val message: String) : LoginSideEffect

    // Информационное сообщение (например, после успешной регистрации)
    data class ShowMessage(val message: String) : LoginSideEffect

    // Уйти на главный экран после успешного входа
    data object NavigateToHome : LoginSideEffect
}
