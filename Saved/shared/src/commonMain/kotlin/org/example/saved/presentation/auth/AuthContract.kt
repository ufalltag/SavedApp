package org.example.saved.presentation.auth

/**
 * Состояние экрана (State).
 *
 * Читаем поля отсюда и просто рисуем по ним UI.
 * Это долговременное состояние. Если экран перевернуть или перерисовать,
 * актуальные данные всегда лежат здесь.
 */
data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,     // Показываем крутилку (ProgressView)
    val isLoginMode: Boolean = true     // true = Вход, false = Регистрация
)

/**
 * Разовые события (Side Effects).
 *
 * Это ивенты типа "выстрелил и забыл" (fire-and-forget).
 * Показываем алерты, снекбары или делаем навигацию.
 * ВАЖНО: Не сохраняйте это в @State в SwiftUI, иначе алерт будет
 * всплывать заново при каждом чихе (фантомный баг перерисовки).
 */
sealed interface AuthSideEffect {
    // Показать ошибку (красный алерт/toast)
    data class ShowError(val message: String) : AuthSideEffect

    // Показать информационное сообщение (зеленый алерт/toast)
    data class ShowMessage(val message: String) : AuthSideEffect

    // Уйти на главный экран (Bookmarks) после успешного входа
    data object NavigateToHome : AuthSideEffect
}