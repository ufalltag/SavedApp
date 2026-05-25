package org.example.saved.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.saved.presentation.auth.AuthSideEffect
import org.example.saved.presentation.auth.AuthViewModel
import org.example.saved.ui.screens.AuthScreen
import org.example.saved.ui.screens.BookmarksScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AuthRoute
    ) {
        authGraph(navController)
        bookmarksGraph()
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<AuthRoute> {
        val authViewModel = koinViewModel<AuthViewModel>()

        LaunchedEffect(Unit) {
            authViewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is AuthSideEffect.NavigateToHome -> {
                        navController.navigate(BookmarksRoute) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    }
                    is AuthSideEffect.ShowError -> {
                        // TODO: Обработка ошибки
                    }
                }
            }
        }

        AuthScreen(viewModel = authViewModel)
    }
}

private fun NavGraphBuilder.bookmarksGraph() {
    composable<BookmarksRoute> {
        BookmarksScreen()
    }
}