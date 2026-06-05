package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.example.saved.presentation.folder.FolderDetailViewModel
import org.example.saved.ui.screens.folderdetails.FolderDetailScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class FolderRoute(
    val id: String,
    val name: String,
)

fun NavGraphBuilder.folderDetailScreen(navController: NavHostController) {
    composable<FolderRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<FolderRoute>()

        val viewModel =
            koinViewModel<FolderDetailViewModel> {
                parametersOf(route.id, route.name)
            }

        FolderDetailScreen(
            viewModel = viewModel,
            folderId = route.id,
            folderName = route.name,
            onBackClick = { navController.popBackStack() },
        )
    }
}
