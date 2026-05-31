package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.example.saved.presentation.folders.AllFoldersViewModel
import org.example.saved.ui.screens.allfolders.AllFoldersScreen
import org.koin.androidx.compose.koinViewModel

// Type-Safe маршрут
@Serializable
object AllFoldersRoute


fun NavGraphBuilder.allFoldersScreen(navController: NavHostController) {
    composable<AllFoldersRoute> {
        val viewModel = koinViewModel<AllFoldersViewModel>()

        AllFoldersScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onFolderClick = { id, name ->
                navController.navigate(FolderRoute(id = id, name = name))
            }
        )
    }
}
