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
import org.example.saved.presentation.home.HomeSideEffect
import org.example.saved.presentation.home.HomeViewModel
import org.example.saved.ui.components.bookmarks.BookmarkItem
import org.example.saved.ui.components.bookmarks.FloatingInputBar
import org.example.saved.ui.components.bookmarks.FolderItem
import org.example.saved.ui.components.bookmarks.ScreenHeader
import org.example.saved.ui.components.bookmarks.SectionTitle
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarksScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onFolderClick: (String, String) -> Unit,
    onSeeAllFoldersClick: () -> Unit
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    // Состояние для диалога предложения папки от ИИ
    var aiFolderSuggestion by remember { mutableStateOf<HomeSideEffect.RequireFolderSelection?>(null) }

    // Слушаем ошибки и другие эффекты прямо здесь
    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is HomeSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is HomeSideEffect.OpenUrl -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Не удалось открыть ссылку")
                    }
                }

                is HomeSideEffect.RequireFolderSelection -> {
                    aiFolderSuggestion = effect
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
                    viewModel.analyzeUrl(url)
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
                    name = state.username ?: stringResource(R.string.bookmarks_header_name),
                    date = stringResource(R.string.bookmarks_header_date)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    title = stringResource(R.string.bookmarks_section_folders_title),
                    actionText = stringResource(R.string.bookmarks_section_folders_action),
                    onActionClick = onSeeAllFoldersClick
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
                        linksCount = folder.bookmarksCount,
                        onClick = { onFolderClick(folder.id, folder.name) },
                        onRenameClick = { viewModel.requestRenameFolder(folder) },
                        onDeleteClick = { viewModel.requestDeleteFolder(folder) }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(title = stringResource(R.string.bookmarks_section_links_title), actionText = null)
            }

            if (state.isBookmarksLoading && state.recentBookmarks.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(
                    items = state.recentBookmarks,
                    span = { GridItemSpan(maxLineSpan) },
                    key = { bookmark -> "bookmark_${bookmark.id}" }
                ) { bookmark ->
                    BookmarkItem(
                        title = bookmark.title,
                        url = bookmark.url,
                        date = "Recently",
                        onClick = { viewModel.openBookmark(bookmark.url) },
                        onDelete = { viewModel.requestDeleteBookmark(bookmark) },
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

        // Диалог подтверждения удаления закладки
        state.bookmarkPendingDelete?.let { bookmark ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteBookmark() },
                title = { Text("Удалить закладку?") },
                text = { Text("Вы уверены, что хотите удалить \"${bookmark.title}\"?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteBookmark() }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteBookmark() }) {
                        Text("Отмена")
                    }
                }
            )
        }

        // Диалог предложения новой папки от ИИ
        aiFolderSuggestion?.let { suggestion ->
            AlertDialog(
                onDismissRequest = { aiFolderSuggestion = null },
                title = { Text("Создать новую папку?") },
                text = { Text("Нейросеть предлагает создать папку \"${suggestion.suggestedFolderName}\" для этой ссылки. Согласны?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.saveToNewFolder(suggestion.url, suggestion.suggestedFolderName ?: "Разное", suggestion.bookmarkTitle)
                        aiFolderSuggestion = null
                    }) { Text("Создать и сохранить") }
                },
                dismissButton = {
                    TextButton(onClick = { aiFolderSuggestion = null }) { Text("Отмена") }
                }
            )
        }

        // Диалог удаления папки
        state.folderPendingDelete?.let { folder ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteFolder() },
                title = { Text("Удалить папку?") },
                text = { Text("Папка \"${folder.name}\" и все её ссылки будут безвозвратно удалены. Это действие нельзя отменить.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteFolder() }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteFolder() }) {
                        Text("Отмена")
                    }
                }
            )
        }

        // Диалог переименования папки
        state.folderPendingRename?.let { folder ->
            var renameText by remember { mutableStateOf(folder.name) }

            AlertDialog(
                onDismissRequest = { viewModel.dismissRenameFolder() },
                title = { Text("Переименовать папку") },
                text = {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        label = { Text("Новое название") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmRenameFolder(renameText) },
                        enabled = renameText.isNotBlank() && renameText != folder.name
                    ) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissRenameFolder() }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
