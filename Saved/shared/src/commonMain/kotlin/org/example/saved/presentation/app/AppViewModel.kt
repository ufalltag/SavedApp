package org.example.saved.presentation.app

import androidx.lifecycle.ViewModel
import org.example.saved.domain.repository.SettingsStorage
import org.example.saved.domain.usecase.IsLoggedInUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class AppState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isDarkMode: Boolean? = null
)

sealed interface AppSideEffect

class AppViewModel(
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val settingsStorage: SettingsStorage
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(AppState()) {
        checkSession()
        loadTheme()
    }

    private fun loadTheme() = intent {
        val savedTheme = settingsStorage.getDarkMode()
        reduce { state.copy(isDarkMode = savedTheme) }
    }
    fun toggleDarkMode(isDark: Boolean?) = intent {
        reduce { state.copy(isDarkMode = isDark) }
        settingsStorage.setDarkMode(isDark)
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

    /** Вызывается после успешного входа — переключает корень на главный экран. */
    fun onAuthenticated() = intent {
        reduce { state.copy(isLoggedIn = true) }
    }

    /** Вызывается после выхода (токены уже очищены в AccountViewModel) — возвращает на экран входа. */
    fun onLoggedOut() = intent {
        reduce { state.copy(isLoggedIn = false) }
    }
}
