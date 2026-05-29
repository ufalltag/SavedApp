package org.example.saved.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.example.saved.presentation.main.BookmarksViewModel
import org.example.saved.ui.screens.BookmarksScreen
import org.koin.androidx.compose.koinViewModel

@Serializable
object BookmarksRoute

fun NavGraphBuilder.bookmarksScreen(navController: NavHostController) {
    composable<BookmarksRoute> {
        val viewModel = koinViewModel<BookmarksViewModel>()
        BookmarksScreen(
            viewModel = viewModel,
            onFolderClick = { id, name ->
                navController.navigate(FolderRoute(id = id, name = name))
            }
        )
    }
}
