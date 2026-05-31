package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.example.saved.presentation.account.AccountViewModel
import org.example.saved.ui.screens.AccountScreen
import org.koin.androidx.compose.koinViewModel

@Serializable
object AccountRoute

fun NavGraphBuilder.accountScreen(navController: NavHostController) {
    composable<AccountRoute> {
        val viewModel = koinViewModel<AccountViewModel>()
         AccountScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onNavigateToLogin = {
                navController.navigate(LoginRoute) {
                    popUpTo(0)
                }
            }
        )
    }
}
