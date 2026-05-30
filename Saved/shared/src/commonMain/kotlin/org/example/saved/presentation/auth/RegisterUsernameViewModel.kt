package org.example.saved.presentation.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.saved.domain.usecase.RegisterUseCase
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel 2-го шага регистрации.
 * email и password переданы из [RegisterCredentialsViewModel] через параметры навигации
 * (в DI приходят как параметры фабрики Koin). Здесь вводится username и делается
 * сам запрос регистрации, после чего пользователь возвращается на экран входа.
 */
class RegisterUsernameViewModel(
    private val registerUseCase: RegisterUseCase,
    private val email: String,
    private val password: String
) : ViewModel(), ContainerHost<RegisterUsernameState, RegisterUsernameSideEffect> {

    override val container =
        container<RegisterUsernameState, RegisterUsernameSideEffect>(RegisterUsernameState())

    val viewStates: StateFlow<RegisterUsernameState> get() = container.stateFlow
    val viewSideEffects: Flow<RegisterUsernameSideEffect> get() = container.sideEffectFlow

    fun onUsernameChanged(username: String) = intent {
        reduce { state.copy(username = username) }
    }

    /**
     * Кнопка "Готово". Регистрируем пользователя и возвращаем на экран входа.
     */
    fun submit() = intent {
        if (state.isLoading) return@intent // Защита от двойного клика

        val username = state.username.trim()
        if (username.isEmpty()) {
            postSideEffect(RegisterUsernameSideEffect.ShowError("Введите имя пользователя"))
            return@intent
        }

        reduce { state.copy(isLoading = true) }
        val result = registerUseCase(email, password, username)
        reduce { state.copy(isLoading = false) }

        result
            .onSuccess {
                postSideEffect(
                    RegisterUsernameSideEffect.NavigateToLogin(
                        "Регистрация прошла успешно! Войдите в аккаунт."
                    )
                )
            }
            .onFailure { error ->
                postSideEffect(
                    RegisterUsernameSideEffect.ShowError(error.message ?: "Неизвестная ошибка")
                )
            }
    }
}
