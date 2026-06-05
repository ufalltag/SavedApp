package org.example.saved.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.R
import org.example.saved.domain.analytics.AnalyticsTracker
import org.example.saved.presentation.folder.FolderDetailSideEffect
import org.example.saved.presentation.folder.FolderDetailViewModel
import org.example.saved.ui.screens.home.components.BookmarkItem
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.jetbrains.compose.resources.painterResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    viewModel: FolderDetailViewModel = koinViewModel(),
    analytics: AnalyticsTracker = koinInject(),
    folderId: String,
    folderName: String,
    onBackClick: () -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        analytics.logScreen("launch_folder")
        analytics.logEvent("folder_opened", mapOf("folder_id" to folderId))
    }

    LaunchedEffect(folderId) {
        viewModel.initFolder(folderId, folderName)
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { effect ->
            when (effect) {
                is FolderDetailSideEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }

                is FolderDetailSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is FolderDetailSideEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.folderName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.bookmarks.isEmpty()) {
                Text(
                    text = stringResource(R.string.folder_detail_empty),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = state.bookmarks,
                        key = { "bookmark_${it.id}" },
                    ) { bookmark ->
                        BookmarkItem(
                            title = bookmark.title,
                            url = bookmark.url,
                            date = stringResource(R.string.bookmark_date_just_now),
                            onClick = { viewModel.onBookmarkClick(bookmark.url) },
                            onDelete = { viewModel.onDeleteBookmark(bookmark.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
