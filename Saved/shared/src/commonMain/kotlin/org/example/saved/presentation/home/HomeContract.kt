package org.example.saved.presentation.home

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

data class HomeState(
    val folders: List<Folder> = emptyList(),
    val recentBookmarks: List<Bookmark> = emptyList(),
    val isFoldersLoading: Boolean = true,
    val isBookmarksLoading: Boolean = true,
    val isAnalyzing: Boolean = false,
    val errorMessage: String? = null,
    val username: String? = null,

    // Bookmark actions
    val bookmarkPendingDelete: Bookmark? = null,
    val bookmarkPendingMove: Bookmark? = null,

    // Folder actions
    val folderPendingDelete: Folder? = null,
    val folderPendingRename: Folder? = null,
)

sealed interface HomeSideEffect {
    data class OpenUrl(val url: String) : HomeSideEffect
    data class ShowError(val message: String) : HomeSideEffect
    data class RequireFolderSelection(
        val url: String,
        val suggestedFolderName: String?
    ) : HomeSideEffect
}
