package org.example.saved.presentation.auth

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.example.saved.domain.repository.AuthRepository
import org.orbitmvi.orbit.viewmodel.container

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<AuthState, AuthSideEffect> {

    override val container = container<AuthState, AuthSideEffect>(AuthState())

    fun onEmailChanged(email: String) = intent {
        reduce { state.copy(email = email) }
    }

    fun onPasswordChanged(password: String) = intent {
        reduce { state.copy(password = password) }
    }

    fun toggleMode() = intent {
        reduce { state.copy(isLoginMode = !state.isLoginMode) }
    }

    fun submit() = intent {
        if (state.isLoading) return@intent

        val isLogin = state.isLoginMode

        reduce { state.copy(isLoading = true) }

        val result = if (isLogin) {
            authRepository.login(state.email, state.password)
        } else {
            authRepository.register(state.email, state.password)
        }

        reduce { state.copy(isLoading = false) }

        result
            .onSuccess {
                if (isLogin) {
                    postSideEffect(AuthSideEffect.NavigateToHome)
                } else {
                    reduce {
                        state.copy(
                            isLoginMode = true,
                            password = ""
                        )
                    }
                    postSideEffect(AuthSideEffect.ShowMessage("Регистрация успешна. Теперь войди в аккаунт."))
                }
            }
            .onFailure { error ->
                postSideEffect(AuthSideEffect.ShowError(error.message ?: "Unknown error"))
            }
    }
}
