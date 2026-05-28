package org.example.saved.presentation.bookmarks

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

data class BookmarksState(
    val folders: List<Folder> = emptyList(),
    val selectedFolderId: String? = null,
    val bookmarks: List<Bookmark> = emptyList(),

    val isFoldersLoading: Boolean = true,
    val isBookmarksLoading: Boolean = false,

    val isAnalyzing: Boolean = false,

    val errorMessage: String? = null
)

sealed interface BookmarksSideEffect {
    data class ShowToast(val message: String) : BookmarksSideEffect
    data class OpenUrl(val url: String) : BookmarksSideEffect
}
