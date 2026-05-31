package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.example.saved.presentation.auth.LoginViewModel
import org.example.saved.presentation.auth.RegisterCredentialsViewModel
import org.example.saved.presentation.auth.RegisterUsernameViewModel
import org.example.saved.ui.screens.LoginScreen
import org.example.saved.ui.screens.RegisterCredentialsScreen
import org.example.saved.ui.screens.RegisterUsernameScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
object LoginRoute

@Serializable
object RegisterCredentialsRoute

@Serializable
data class RegisterUsernameRoute(val email: String, val password: String)

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<LoginRoute> {
        val viewModel = koinViewModel<LoginViewModel>()
        LoginScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navController.navigate(BookmarksRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(RegisterCredentialsRoute)
            }
        )
    }

    composable<RegisterCredentialsRoute> {
        val viewModel = koinViewModel<RegisterCredentialsViewModel>()
        RegisterCredentialsScreen(
            viewModel = viewModel,
            onNavigateToUsername = { email, password ->
                navController.navigate(RegisterUsernameRoute(email, password))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<RegisterUsernameRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<RegisterUsernameRoute>()
        val viewModel = koinViewModel<RegisterUsernameViewModel> {
            parametersOf(route.email, route.password)
        }

        RegisterUsernameScreen(
            viewModel = viewModel,
            onNavigateToLogin = { message ->
                navController.navigate(LoginRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }
}
