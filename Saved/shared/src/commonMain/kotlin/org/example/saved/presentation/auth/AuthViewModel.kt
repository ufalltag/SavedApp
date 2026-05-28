package org.example.saved.presentation.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.saved.domain.repository.AuthRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * Общая логика авторизации для Android и iOS.
 *
 * Правила для iOS (SwiftUI):
 * 1. UI — это "глупое железо", не пишите логику во View.
 * 2. Слушайте `stateFlow`, чтобы отрисовывать тексты и лоадер.
 * 3. Слушайте `sideEffectFlow`, чтобы ловить команды на переход и алерты.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<AuthState, AuthSideEffect> {

    override val container = container<AuthState, AuthSideEffect>(AuthState())

    // Типизированные свойства для iOS/SKIE — без них SKIE теряет тип AuthState/AuthSideEffect
    // при экспорте через нетипизированный ContainerHost.container
    val viewStates: StateFlow<AuthState> get() = container.stateFlow
    val viewSideEffects: Flow<AuthSideEffect> get() = container.sideEffectFlow

    /**
     * Вызывать при каждом изменении символа в поле Email.
     */
    fun onEmailChanged(email: String) = intent {
        reduce { state.copy(email = email) }
    }

    /**
     * Вызывать при каждом изменении символа в поле Password.
     */
    fun onPasswordChanged(password: String) = intent {
        reduce { state.copy(password = password) }
    }

    /**
     * Переключатель интерфейса: Вход <-> Регистрация.
     * Не делает сетевых запросов, только меняет UI.
     */
    fun toggleMode() = intent {
        reduce { state.copy(isLoginMode = !state.isLoginMode) }
    }

    /**
     * Главная кнопка действия ("Войти" или "Зарегистрироваться").
     *
     * - Защищена от двойного клика (проверка isLoading).
     * - Успех или ошибка прилетят НЕ как return, а асинхронно
     *   в `sideEffectFlow` (как NavigateToHome или ShowError).
     */
    fun submit() = intent {
        if (state.isLoading) return@intent // Блокируем спам кликами

        reduce { state.copy(isLoading = true) }

        val result = if (state.isLoginMode) {
            authRepository.login(state.email, state.password)
        } else {
            authRepository.register(state.email, state.password)
        }

        reduce { state.copy(isLoading = false) }

        // Распределяем результаты в разовые эффекты (SideEffects)
        result
            .onSuccess {
                postSideEffect(AuthSideEffect.NavigateToHome)
            }
            .onFailure { error ->
                postSideEffect(AuthSideEffect.ShowError(error.message ?: "Неизвестная ошибка"))
            }
    }
}
