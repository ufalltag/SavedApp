package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.example.saved.ui.screens.FolderDetailScreen

@Serializable
data class FolderRoute(val id: String, val name: String)

fun NavGraphBuilder.folderDetailScreen(navController: NavHostController) {
    composable<FolderRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<FolderRoute>()

        FolderDetailScreen(
            folderId = route.id,
            folderName = route.name,
            onBackClick = { navController.popBackStack() }
        )
    }
}
