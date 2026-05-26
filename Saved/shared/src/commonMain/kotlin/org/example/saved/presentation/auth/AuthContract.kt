package org.example.saved.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginMode: Boolean = true,
    val errorMessage: String? = null,
)

sealed interface AuthSideEffect {
    data class ShowError(val message: String) : AuthSideEffect
    data class ShowMessage(val message: String) : AuthSideEffect
    object NavigateToHome : AuthSideEffect
}

