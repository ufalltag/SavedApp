package org.example.saved.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.example.saved.presentation.app.AppViewModel
import org.koin.androidx.compose.koinViewModel

// В файле AppNavigation.kt
@Composable
fun AppNavigation(
    appViewModel: AppViewModel, // ПАТЧ: Принимаем как аргумент
    navController: NavHostController = rememberNavController(),
) {
    val state by appViewModel.container.stateFlow.collectAsStateWithLifecycle()

    if (state.isCheckingSession) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (state.isLoggedIn) BookmarksRoute else LoginRoute,
        ) {
            authGraph(navController)
            bookmarksScreen(navController)
            folderDetailScreen(navController)
            accountScreen(navController, appViewModel)
        }
    }
}
