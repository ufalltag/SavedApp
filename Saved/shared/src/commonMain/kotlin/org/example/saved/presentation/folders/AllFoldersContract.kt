package org.example.saved.presentation.folders

import org.example.saved.domain.model.Folder

data class AllFoldersState(
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentPage: Int = 0,
)

sealed interface AllFoldersSideEffect {
    data class ShowError(
        val message: String,
    ) : AllFoldersSideEffect
}
