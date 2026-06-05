package org.example.saved.ui.screens.folderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.example.saved.R
import org.example.saved.presentation.folder.FolderDetailState
import org.example.saved.ui.screens.home.components.BookmarkItem

@Composable
fun FolderDetailContent(
    state: FolderDetailState,
    modifier: Modifier = Modifier,
    onBookmarkClick: (String) -> Unit,
    onDeleteBookmark: (String) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.bookmarks.isEmpty() -> {
                Text(
                    text = stringResource(R.string.folder_detail_empty),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }

            else -> {
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
                            onClick = { onBookmarkClick(bookmark.url) },
                            onDelete = { onDeleteBookmark(bookmark.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
