package org.example.saved.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import org.example.saved.presentation.main.BookmarksSideEffect
import org.example.saved.presentation.main.BookmarksViewModel
import org.example.saved.ui.components.bookmarks.BookmarkItem
import org.example.saved.ui.components.bookmarks.FloatingInputBar
import org.example.saved.ui.components.bookmarks.FolderItem
import org.example.saved.ui.components.bookmarks.ScreenHeader
import org.example.saved.ui.components.bookmarks.SectionTitle
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = koinViewModel(),
    onFolderClick: (String, String) -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    // Слушаем ошибки и другие эффекты прямо здесь
    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is BookmarksSideEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is BookmarksSideEffect.NavigateToFolder -> {
                    onFolderClick(effect.folderId, effect.folderName)
                }
                is BookmarksSideEffect.OpenUrl -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Не удалось открыть ссылку")
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
                isAnalyzing = state.isAnalyzing,
                onSendClick = { url ->
                    viewModel.analyzeAndSaveUrl(url)
                },
            )
        },
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ScreenHeader(
                    name = stringResource(R.string.bookmarks_header_name),
                    date = stringResource(R.string.bookmarks_header_date)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    title = stringResource(R.string.bookmarks_section_folders_title),
                    actionText = stringResource(R.string.bookmarks_section_folders_action)
                )
            }

            item {
                FolderItem(
                    title = stringResource(R.string.bookmarks_add_folder_title),
                    linksCount = null,
                    onClick = {
                        newFolderName = ""
                        showCreateFolderDialog = true
                    },
                )
            }

            if (state.isFoldersLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(
                    items = state.folders,
                    key = { folder -> "folder_${folder.id}" }
                ) { folder ->
                    FolderItem(
                        title = folder.name,
                        linksCount = 0,
                        onClick = { viewModel.onFolderClick(folder.id, folder.name) }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(title = stringResource(R.string.bookmarks_section_links_title), actionText = null)
            }

            if (state.isBookmarksLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(
                    items = state.bookmarks,
                    span = { GridItemSpan(maxLineSpan) },
                    key = { bookmark -> "bookmark_${bookmark.id}" }
                ) { bookmark ->
                    BookmarkItem(
                        title = bookmark.title,
                        url = bookmark.url,
                        date = stringResource(R.string.bookmark_date_placeholder),
                        onClick = { viewModel.onBookmarkClick(bookmark.url) },
                        onDelete = { viewModel.deleteBookmark(bookmark.id) },
                    )
                }
            }
        }

        if (showCreateFolderDialog) {
            AlertDialog(
                onDismissRequest = { showCreateFolderDialog = false },
                title = { Text(text = stringResource(R.string.dialog_create_folder_title)) },
                text = {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text(stringResource(R.string.dialog_create_folder_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFolderName.isNotBlank()) {
                                viewModel.createFolder(newFolderName)
                                showCreateFolderDialog = false
                            }
                        },
                        enabled = newFolderName.isNotBlank(),
                    ) {
                        Text(stringResource(R.string.dialog_create_folder_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateFolderDialog = false }) {
                        Text(stringResource(R.string.dialog_create_folder_dismiss))
                    }
                },
            )
        }
    }
}
