package org.example.saved.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.example.saved.R
import org.example.saved.domain.analytics.AnalyticsTracker
import org.example.saved.presentation.home.HomeSideEffect
import org.example.saved.presentation.home.HomeViewModel
import org.example.saved.ui.screens.home.components.BookmarkItem
import org.example.saved.ui.screens.home.components.FloatingInputBar
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    analyticsTracker: AnalyticsTracker = koinInject(),
    onFolderClick: (String, String) -> Unit,
    onSeeAllFoldersClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var aiFolderSuggestion by remember { mutableStateOf<HomeSideEffect.RequireFolderSelection?>(null) }

    val errorOpenLinkUrl = stringResource(R.string.error_open_link)

    LaunchedEffect(Unit) {
        analyticsTracker.logScreen("launch_home")
    }

    LaunchedEffect(state.isSearchMode) {
        if (state.isSearchMode) analyticsTracker.logScreen("launch_search")
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is HomeSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is HomeSideEffect.RequireFolderSelection -> {
                    aiFolderSuggestion = effect
                }

                is HomeSideEffect.OpenUrl -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(errorOpenLinkUrl)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            FloatingInputBar(
                text = inputText,
                onTextChange = { newText ->
                    inputText = newText
                    if (state.isSearchMode) viewModel.onSearchQueryChanged(newText)
                },
                isAnalyzing = state.isAnalyzing,
                isSearchMode = state.isSearchMode,
                onSendClick = {
                    viewModel.analyzeUrl(it)
                    inputText = ""
                },
                onSearchToggle = {
                    val newSearchMode = !state.isSearchMode
                    viewModel.toggleSearchMode(newSearchMode)
                    if (!newSearchMode) inputText = ""
                },
            )
        },
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        if (state.isSearchMode) {
            HomeSearchContent(
                state = state,
                inputText = inputText,
                modifier = contentModifier,
                onBookmarkClick = { viewModel.openBookmark(it) },
                onDeleteBookmark = { viewModel.requestDeleteBookmark(it) },
            )
        } else {
            HomeGridContent(
                state = state,
                modifier = contentModifier,
                onProfileClick = onProfileClick,
                onSeeAllFoldersClick = onSeeAllFoldersClick,
                onCreateFolderClick = { showCreateFolderDialog = true },
                onFolderClick = onFolderClick,
                onRenameFolderClick = { viewModel.requestRenameFolder(it) },
                onDeleteFolderClick = { viewModel.requestDeleteFolder(it) },
                onBookmarkClick = { viewModel.openBookmark(it) },
                onDeleteBookmarkClick = { viewModel.requestDeleteBookmark(it) },
            )
        }

        HomeDialogs(
            showCreateFolderDialog = showCreateFolderDialog,
            bookmarkPendingDelete = state.bookmarkPendingDelete,
            folderPendingDelete = state.folderPendingDelete,
            folderPendingRename = state.folderPendingRename,
            aiFolderSuggestion = aiFolderSuggestion,
            onCreateFolderDismiss = { showCreateFolderDialog = false },
            onCreateFolderConfirm = {
                viewModel.createFolder(it)
                showCreateFolderDialog = false
            },
            onDeleteBookmarkDismiss = { viewModel.dismissDeleteBookmark() },
            onDeleteBookmarkConfirm = { viewModel.confirmDeleteBookmark() },
            onDeleteFolderDismiss = { viewModel.dismissDeleteFolder() },
            onDeleteFolderConfirm = { viewModel.confirmDeleteFolder() },
            onRenameFolderDismiss = { viewModel.dismissRenameFolder() },
            onRenameFolderConfirm = { viewModel.confirmRenameFolder(it) },
            onAiSuggestionDismiss = { aiFolderSuggestion = null },
            onAiSuggestionConfirm = { url, folder, title ->
                viewModel.saveToNewFolder(url, folder, title)
                aiFolderSuggestion = null
            },
        )
    }
}

@Composable
private fun HomeSearchContent(
    state: org.example.saved.presentation.home.HomeState,
    inputText: String,
    modifier: Modifier = Modifier,
    onBookmarkClick: (String) -> Unit,
    onDeleteBookmark: (org.example.saved.domain.model.Bookmark) -> Unit,
) {
    when {
        state.isSearching -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        inputText.isBlank() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.search_empty_input_hint),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }

        state.searchResults.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.search_no_results),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                items(items = state.searchResults, key = { "search_${it.id}" }) { bookmark ->
                    BookmarkItem(
                        title = bookmark.title,
                        url = bookmark.url,
                        date = stringResource(R.string.bookmark_date_search_result),
                        onClick = { onBookmarkClick(bookmark.url) },
                        onDelete = { onDeleteBookmark(bookmark) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}
