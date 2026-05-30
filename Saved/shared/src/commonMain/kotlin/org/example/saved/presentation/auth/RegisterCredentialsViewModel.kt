package org.example.saved.presentation.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel 1-го шага регистрации.
 * Собирает email/пароль, валидирует и передаёт их на 2-й шаг через side effect.
 * Сам запрос регистрации делает [RegisterUsernameViewModel].
 */
class RegisterCredentialsViewModel : ViewModel(),
    ContainerHost<RegisterCredentialsState, RegisterCredentialsSideEffect> {

    override val container =
        container<RegisterCredentialsState, RegisterCredentialsSideEffect>(RegisterCredentialsState())

    val viewStates: StateFlow<RegisterCredentialsState> get() = container.stateFlow
    val viewSideEffects: Flow<RegisterCredentialsSideEffect> get() = container.sideEffectFlow

    fun onEmailChanged(email: String) = intent {
        reduce { state.copy(email = email) }
    }

    fun onPasswordChanged(password: String) = intent {
        reduce { state.copy(password = password) }
    }

    /**
     * Кнопка "Далее". Проверяем поля и переходим на экран ввода username.
     */
    fun next() = intent {
        val email = state.email.trim()
        val password = state.password

        when {
            email.isEmpty() ->
                postSideEffect(RegisterCredentialsSideEffect.ShowError("Введите email"))
            password.isEmpty() ->
                postSideEffect(RegisterCredentialsSideEffect.ShowError("Введите пароль"))
            else ->
                postSideEffect(RegisterCredentialsSideEffect.NavigateToUsername(email, password))
        }
    }
}
