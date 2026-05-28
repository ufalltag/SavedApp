package org.example.saved.presentation.folder

import org.example.saved.domain.model.Bookmark

data class FolderDetailState(
    val folderId: String = "",
    val folderName: String = "",
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDeleting: Boolean = false
)

sealed interface FolderDetailSideEffect {
    data class ShowError(val message: String) : FolderDetailSideEffect
    data class ShowMessage(val message: String) : FolderDetailSideEffect
    data class OpenUrl(val url: String) : FolderDetailSideEffect
}
