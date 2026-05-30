package org.example.saved.presentation.folderlinks

import androidx.lifecycle.ViewModel
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.usecase.DeleteBookmarkUseCase
import org.example.saved.domain.usecase.GetBookmarksUseCase
import org.example.saved.domain.usecase.GetFoldersUseCase
import org.example.saved.domain.usecase.UpdateBookmarkUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

private const val PAGE_SIZE = 20

class FolderLinksViewModel(
    private val folderId: String,
    private val folderName: String,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
) : ViewModel(), ContainerHost<FolderLinksState, FolderLinksSideEffect> {

    override val container: Container<FolderLinksState, FolderLinksSideEffect> =
        container(FolderLinksState(folderName = folderName)) {
            loadFirstPage()
            loadFolders()
        }

    private fun loadFirstPage() = intent {
        reduce { state.copy(isLoading = true) }

        getBookmarksUseCase(folderId = folderId, page = 1, limit = PAGE_SIZE).onSuccess { bookmarks ->
            reduce {
                state.copy(
                    bookmarks = bookmarks,
                    isLoading = false,
                    currentPage = 1,
                    hasMore = bookmarks.size >= PAGE_SIZE
                )
            }
        }.onFailure { error ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(FolderLinksSideEffect.ShowError(error.message ?: "Ошибка загрузки ссылок"))
        }
    }

    private fun loadFolders() = intent {
        getFoldersUseCase(page = 1, limit = 100).onSuccess { folders ->
            reduce { state.copy(folders = folders) }
        }
    }

    fun loadMore() = intent {
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return@intent
        val nextPage = state.currentPage + 1
        reduce { state.copy(isLoadingMore = true) }

        getBookmarksUseCase(folderId = folderId, page = nextPage, limit = PAGE_SIZE).onSuccess { newBookmarks ->
            reduce {
                state.copy(
                    bookmarks = state.bookmarks + newBookmarks,
                    isLoadingMore = false,
                    currentPage = nextPage,
                    hasMore = newBookmarks.size >= PAGE_SIZE
                )
            }
        }.onFailure { error ->
            reduce { state.copy(isLoadingMore = false) }
            postSideEffect(FolderLinksSideEffect.ShowError(error.message ?: "Ошибка загрузки ссылок"))
        }
    }

    fun requestDeleteBookmark(bookmark: Bookmark) = intent {
        reduce { state.copy(bookmarkPendingDelete = bookmark) }
    }

    fun dismissDeleteBookmark() = intent {
        reduce { state.copy(bookmarkPendingDelete = null) }
    }

    fun confirmDeleteBookmark() = intent {
        val bookmark = state.bookmarkPendingDelete ?: return@intent
        reduce { state.copy(bookmarkPendingDelete = null) }
        deleteBookmarkUseCase(bookmark.id).onSuccess {
            reduce { state.copy(bookmarks = state.bookmarks.filter { it.id != bookmark.id }) }
        }.onFailure { error ->
            postSideEffect(FolderLinksSideEffect.ShowError(error.message ?: "Ошибка удаления ссылки"))
        }
    }

    fun requestMoveBookmark(bookmark: Bookmark) = intent {
        reduce { state.copy(bookmarkPendingMove = bookmark) }
    }

    fun dismissMoveBookmark() = intent {
        reduce { state.copy(bookmarkPendingMove = null) }
    }

    fun confirmMoveBookmark(targetFolderId: String) = intent {
        val bookmark = state.bookmarkPendingMove ?: return@intent
        reduce { state.copy(bookmarkPendingMove = null) }
        updateBookmarkUseCase(bookmarkId = bookmark.id, folderId = targetFolderId).onSuccess {
            reduce { state.copy(bookmarks = state.bookmarks.filter { it.id != bookmark.id }) }
        }.onFailure { error ->
            postSideEffect(FolderLinksSideEffect.ShowError(error.message ?: "Ошибка перемещения ссылки"))
        }
    }
}
