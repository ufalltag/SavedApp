package org.example.saved.ui.screens.allfolders

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.domain.analytics.AnalyticsTracker
import org.example.saved.presentation.folders.AllFoldersSideEffect
import org.example.saved.presentation.folders.AllFoldersViewModel
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AllFoldersScreen(
    onBackClick: () -> Unit,
    onFolderClick: (String, String) -> Unit,
    viewModel: AllFoldersViewModel = koinViewModel(),
    analyticsTracker: AnalyticsTracker = koinInject(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        analyticsTracker.logScreen("launch_all_folders")
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { effect ->
            when (effect) {
                is AllFoldersSideEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AllFoldersTopBar(onBackClick = onBackClick)
        },
    ) { paddingValues ->
        AllFoldersGrid(
            state = state,
            onFolderClick = onFolderClick,
            onLoadMore = viewModel::loadMore,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        )
    }
}
