package org.example.saved.ui.screens.folderdetails

import android.content.Intent
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.R
import org.example.saved.domain.analytics.AnalyticsTracker
import org.example.saved.presentation.folder.FolderDetailSideEffect
import org.example.saved.presentation.folder.FolderDetailViewModel
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.jetbrains.compose.resources.painterResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderId: String,
    folderName: String,
    onBackClick: () -> Unit,
    viewModel: FolderDetailViewModel = koinViewModel(),
    analyticsTracker: AnalyticsTracker = koinInject(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(folderId) {
        viewModel.initFolder(folderId, folderName)
        analyticsTracker.logScreen("launch_folder_detail")
        analyticsTracker.logEvent("folder_opened", mapOf("folder_id" to folderId))
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { effect ->
            when (effect) {
                is FolderDetailSideEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }

                is FolderDetailSideEffect.ShowError,
                is FolderDetailSideEffect.ShowMessage,
                -> {
                    val message =
                        if (effect is FolderDetailSideEffect.ShowError) {
                            effect.message
                        } else {
                            (effect as FolderDetailSideEffect.ShowMessage)
                                .message
                        }
                    snackbarHostState.showSnackbar(message)
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
        FolderDetailContent(
            state = state,
            modifier = Modifier.padding(paddingValues),
            onBookmarkClick = viewModel::onBookmarkClick,
            onDeleteBookmark = viewModel::onDeleteBookmark,
        )
    }
}
