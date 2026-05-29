package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.example.saved.presentation.auth.AuthViewModel
import org.example.saved.ui.screens.AuthScreen
import org.koin.androidx.compose.koinViewModel

@Serializable
object AuthRoute

fun NavGraphBuilder.authScreen(navController: NavHostController) {
    composable<AuthRoute> {
        val viewModel = koinViewModel<AuthViewModel>()

        AuthScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navController.navigate(BookmarksRoute) {
                    popUpTo(AuthRoute) { inclusive = true }
                }
            }
        )
    }
}
