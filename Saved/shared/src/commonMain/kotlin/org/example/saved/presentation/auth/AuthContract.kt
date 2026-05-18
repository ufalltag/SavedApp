package org.example.saved.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginMode: Boolean = true
)

sealed interface AuthSideEffect {
    data class ShowError(val message: String) : AuthSideEffect
    object NavigateToHome : AuthSideEffect
}
