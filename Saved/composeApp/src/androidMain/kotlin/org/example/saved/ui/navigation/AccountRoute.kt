// В файле AccountRoute.kt
package org.example.saved.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.example.saved.presentation.account.AccountViewModel
import org.example.saved.presentation.app.AppViewModel
import org.example.saved.ui.screens.AccountScreen
import org.koin.androidx.compose.koinViewModel

@Serializable
object AccountRoute

fun NavGraphBuilder.accountScreen(
    navController: NavHostController,
    appViewModel: AppViewModel,
) {
    composable<AccountRoute> {
        val accountViewModel = koinViewModel<AccountViewModel>()
        val appState by appViewModel.container.stateFlow.collectAsStateWithLifecycle()

        AccountScreen(
            viewModel = accountViewModel,
            isDarkMode = appState.isDarkMode ?: isSystemInDarkTheme(),
            onThemeToggle = { appViewModel.toggleDarkMode(it) },
            onBackClick = { navController.popBackStack() },
            onNavigateToLogin = {
                navController.navigate(LoginRoute) {
                    popUpTo(0)
                }
            },
        )
    }
}
