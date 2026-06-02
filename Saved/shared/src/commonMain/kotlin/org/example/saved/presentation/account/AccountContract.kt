package org.example.saved.presentation.account

data class AccountState(
    val email: String = "",
    val isLoading: Boolean = true,
    val isChangingPassword: Boolean = false,
)

sealed interface AccountSideEffect {
    data class ShowError(
        val message: String,
    ) : AccountSideEffect

    data object PasswordChanged : AccountSideEffect

    data object LoggedOut : AccountSideEffect
}
