package org.example.saved.presentation.app

import androidx.lifecycle.ViewModel
import org.example.saved.domain.usecase.IsLoggedInUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class AppState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false
)

sealed interface AppSideEffect

class AppViewModel(
    private val isLoggedInUseCase: IsLoggedInUseCase
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(AppState()) {
        checkSession()
    }

    private fun checkSession() = intent {
        val loggedIn = isLoggedInUseCase()

        reduce {
            state.copy(
                isCheckingSession = false,
                isLoggedIn = loggedIn
            )
        }
    }
}