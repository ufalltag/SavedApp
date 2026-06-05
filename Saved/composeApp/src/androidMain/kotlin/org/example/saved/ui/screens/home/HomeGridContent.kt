package org.example.saved.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.example.saved.R
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.presentation.home.HomeState
import org.example.saved.ui.screens.home.components.BookmarkItem
import org.example.saved.ui.screens.home.components.FolderItem
import org.example.saved.ui.screens.home.components.ScreenHeader
import org.example.saved.ui.screens.home.components.SectionTitle

@Composable
fun HomeGridContent(
    state: HomeState,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    onSeeAllFoldersClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onFolderClick: (String, String) -> Unit,
    onRenameFolderClick: (Folder) -> Unit,
    onDeleteFolderClick: (Folder) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onDeleteBookmarkClick: (Bookmark) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ScreenHeader(
                name = state.username ?: stringResource(R.string.bookmarks_header_name),
                date = stringResource(R.string.bookmarks_header_date),
                onAvatarClick = onProfileClick,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle(
                title = stringResource(R.string.bookmarks_section_folders_title),
                actionText = stringResource(R.string.bookmarks_section_folders_action),
                onActionClick = onSeeAllFoldersClick,
            )
        }

        item {
            FolderItem(
                title = stringResource(R.string.bookmarks_add_folder_title),
                linksCount = null,
                onClick = onCreateFolderClick,
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
                key = { folder -> "folder_${folder.id}" },
            ) { folder ->
                FolderItem(
                    title = folder.name,
                    linksCount = folder.bookmarksCount,
                    onClick = { onFolderClick(folder.id, folder.name) },
                    onRenameClick = { onRenameFolderClick(folder) },
                    onDeleteClick = { onDeleteFolderClick(folder) },
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
                key = { bookmark -> "bookmark_${bookmark.id}" },
            ) { bookmark ->
                BookmarkItem(
                    title = bookmark.title,
                    url = bookmark.url,
                    date = stringResource(R.string.bookmark_date_recently),
                    onClick = { onBookmarkClick(bookmark.url) },
                    onDelete = { onDeleteBookmarkClick(bookmark) },
                )
            }
        }
    }
}
