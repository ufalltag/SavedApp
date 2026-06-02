package org.example.saved.presentation.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.saved.domain.usecase.LoginUseCase
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel экрана Входа. Отвечает только за вход существующего пользователя.
 * Регистрация вынесена в отдельные экраны/ViewModel.
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel(),
    ContainerHost<LoginState, LoginSideEffect> {
    override val container = container<LoginState, LoginSideEffect>(LoginState())

    // Типизированные свойства для iOS/SKIE.
    val viewStates: StateFlow<LoginState> get() = container.stateFlow
    val viewSideEffects: Flow<LoginSideEffect> get() = container.sideEffectFlow

    fun onEmailChanged(email: String) =
        intent {
            reduce { state.copy(email = email) }
        }

    fun onPasswordChanged(password: String) =
        intent {
            reduce { state.copy(password = password) }
        }

    /**
     * Кнопка "Войти". Успех/ошибка прилетают асинхронно в sideEffectFlow.
     */
    fun submit() =
        intent {
            if (state.isLoading) return@intent // Защита от двойного клика

            reduce { state.copy(isLoading = true) }
            val result = loginUseCase(state.email, state.password)
            reduce { state.copy(isLoading = false) }

            result
                .onSuccess { postSideEffect(LoginSideEffect.NavigateToHome) }
                .onFailure { error ->
                    postSideEffect(LoginSideEffect.ShowError(error.message ?: "Неизвестная ошибка"))
                }
        }
}
