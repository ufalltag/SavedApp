package org.example.saved.presentation.account

import androidx.lifecycle.ViewModel
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.usecase.ChangePasswordUseCase
import org.example.saved.domain.usecase.GetProfileUseCase
import org.example.saved.domain.usecase.IsLoggedInUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class AccountViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val authRepository: AuthRepository,
) : ViewModel(),
    ContainerHost<AccountState, AccountSideEffect> {
    override val container: Container<AccountState, AccountSideEffect> =
        container(AccountState()) {
            loadProfile()
        }

    private fun loadProfile() =
        intent {
            reduce { state.copy(isLoading = true) }
            getProfileUseCase()
                .onSuccess { profile ->
                    reduce { state.copy(email = profile.email, isLoading = false) }
                }.onFailure { error ->
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(AccountSideEffect.ShowError(error.message ?: "Ошибка загрузки профиля"))
                }
        }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) = intent {
        if (oldPassword.isBlank() || newPassword.isBlank()) return@intent
        reduce { state.copy(isChangingPassword = true) }
        changePasswordUseCase(oldPassword, newPassword)
            .onSuccess {
                reduce { state.copy(isChangingPassword = false) }
                postSideEffect(AccountSideEffect.PasswordChanged)
            }.onFailure { error ->
                reduce { state.copy(isChangingPassword = false) }
                postSideEffect(AccountSideEffect.ShowError(error.message ?: "Ошибка смены пароля"))
            }
    }

    fun logout() =
        intent {
            authRepository.logout()
            postSideEffect(AccountSideEffect.LoggedOut)
        }
}
