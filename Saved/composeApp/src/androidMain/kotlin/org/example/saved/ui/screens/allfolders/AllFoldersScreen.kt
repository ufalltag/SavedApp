package org.example.saved.ui.screens.allfolders

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.example.saved.presentation.folders.AllFoldersSideEffect
import org.example.saved.presentation.folders.AllFoldersViewModel
import org.example.saved.ui.screens.folders.AllFoldersGrid
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllFoldersScreen(
    viewModel: AllFoldersViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onFolderClick: (String, String) -> Unit
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    // Подписка на шину событий (сайд-эффекты)
    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is AllFoldersSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllFoldersTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        AllFoldersGrid(
            state = state,
            onFolderClick = onFolderClick,
            onLoadMore = viewModel::loadMore,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
