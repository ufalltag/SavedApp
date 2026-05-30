package org.example.saved.presentation.folderlinks

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

data class FolderLinksState(
    val bookmarks: List<Bookmark> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentPage: Int = 0,
    val folderName: String = "",
    val bookmarkPendingDelete: Bookmark? = null,
    val bookmarkPendingMove: Bookmark? = null,
)

sealed interface FolderLinksSideEffect {
    data class ShowError(val message: String) : FolderLinksSideEffect
}
