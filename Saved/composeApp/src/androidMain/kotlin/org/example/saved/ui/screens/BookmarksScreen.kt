package org.example.saved.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.presentation.bookmarks.BookmarksViewModel
import org.example.saved.ui.components.bookmarks.BookmarkItem
import org.example.saved.ui.components.bookmarks.FloatingInputBar
import org.example.saved.ui.components.bookmarks.FolderItem
import org.example.saved.ui.components.bookmarks.ScreenHeader
import org.example.saved.ui.components.bookmarks.SectionTitle
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarksScreen(viewModel: BookmarksViewModel = koinViewModel()) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

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
                ScreenHeader(name = "Artur", date = "Sunday, 17 May")
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = "My folders", actionText = "See all >")
            }

            item {
                FolderItem(
                    title = "Add folder",
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
                        onClick = { viewModel.selectFolder(folder.id) }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(title = "Last links", actionText = null)
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
                        date = "16.05.2026",
                        onClick = { /* TODO: Открыть URL в браузере */ },
                        onDelete = { viewModel.deleteBookmark(bookmark.id) },
                    )
                }
            }
        }

        if (showCreateFolderDialog) {
            AlertDialog(
                onDismissRequest = { showCreateFolderDialog = false },
                title = { Text(text = "Создать папку") },
                text = {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Название папки") },
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
                        Text("Создать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateFolderDialog = false }) {
                        Text("Отмена")
                    }
                },
            )
        }
    }
}
