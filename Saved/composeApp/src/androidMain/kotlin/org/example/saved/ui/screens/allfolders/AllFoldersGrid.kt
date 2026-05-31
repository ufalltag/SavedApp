package org.example.saved.ui.screens.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.saved.presentation.folders.AllFoldersState
import org.example.saved.ui.components.bookmarks.FolderItem

@Composable
fun AllFoldersGrid(
    state: AllFoldersState,
    onFolderClick: (String, String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading && state.folders.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 2. Основная сетка
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = state.folders,
            key = { _, folder -> "all_folder_${folder.id}" }
        ) { index, folder ->

            if (index == state.folders.lastIndex && !state.isLoadingMore && state.hasMore) {
                LaunchedEffect(folder.id) {
                    onLoadMore()
                }
            }

            FolderItem(
                title = folder.name,
                linksCount = folder.bookmarksCount,
                onClick = { onFolderClick(folder.id, folder.name) }
            )
        }

        if (state.isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
