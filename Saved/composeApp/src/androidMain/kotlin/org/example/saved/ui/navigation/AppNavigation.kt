package org.example.saved.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import org.example.saved.presentation.app.AppViewModel
import org.example.saved.presentation.auth.AuthSideEffect
import org.example.saved.presentation.auth.AuthViewModel
import org.example.saved.ui.screens.AuthScreen
import org.example.saved.ui.screens.BookmarksScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val appViewModel = koinViewModel<AppViewModel>()
    val state by appViewModel.container.stateFlow.collectAsStateWithLifecycle()

    if (state.isCheckingSession) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (state.isLoggedIn) BookmarksRoute else AuthRoute,
        ) {
            authGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
            )
            bookmarksGraph()
        }
    }
}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    composable<AuthRoute> {
        val authViewModel = koinViewModel<AuthViewModel>()

        LaunchedEffect(Unit) {
            authViewModel.container.sideEffectFlow.collectLatest { effect ->
                when (effect) {
                    is AuthSideEffect.NavigateToHome -> {
                        navController.navigate(BookmarksRoute) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    }

                    is AuthSideEffect.ShowError -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long,
                        )
                    }

                    is AuthSideEffect.ShowMessage -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short,
                        )
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
