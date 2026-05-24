package org.example.saved.presentation.bookmarks

import androidx.lifecycle.ViewModel
import org.example.saved.domain.repository.BookmarkRepository
import org.example.saved.domain.usecase.SaveAnalyzedBookmarkUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class BookmarksViewModel(
    private val repository: BookmarkRepository,
    private val saveAnalyzedBookmarkUseCase: SaveAnalyzedBookmarkUseCase
) : ViewModel(), ContainerHost<BookmarksState, BookmarksSideEffect> {

    override val container: Container<BookmarksState, BookmarksSideEffect> =
        container(BookmarksState()) {
            loadFolders()
        }

    private fun loadFolders() = intent {
        reduce { state.copy(isFoldersLoading = true, errorMessage = null) }

        repository.getFolders().onSuccess { folders ->
            val firstFolderId = folders.firstOrNull()?.id
            reduce {
                state.copy(
                    folders = folders,
                    isFoldersLoading = false,
                    selectedFolderId = firstFolderId
                )
            }
            if (firstFolderId != null) {
                loadBookmarks(firstFolderId)
            }
        }.onFailure { error ->
            reduce { state.copy(isFoldersLoading = false, errorMessage = error.message) }
            postSideEffect(BookmarksSideEffect.ShowToast("Ошибка загрузки папок"))
        }
    }

    fun selectFolder(folderId: String) = intent {
        if (state.selectedFolderId == folderId) return@intent

        reduce { state.copy(selectedFolderId = folderId) }
        loadBookmarks(folderId)
    }

    private fun loadBookmarks(folderId: String) = intent {
        reduce { state.copy(isBookmarksLoading = true, errorMessage = null) }

        repository.getBookmarks(folderId).onSuccess { bookmarks ->
            reduce { state.copy(bookmarks = bookmarks, isBookmarksLoading = false) }
        }.onFailure { error ->
            reduce { state.copy(isBookmarksLoading = false, errorMessage = error.message) }
            postSideEffect(BookmarksSideEffect.ShowToast("Ошибка загрузки закладок"))
        }
    }

    fun analyzeAndSaveUrl(url: String) = intent {
        if (state.isAnalyzing) return@intent
        reduce { state.copy(isAnalyzing = true) }

        saveAnalyzedBookmarkUseCase(url = url).onSuccess {
            postSideEffect(BookmarksSideEffect.ShowToast("Закладка успешно сохранена!"))
            reduce { state.copy(isAnalyzing = false) }

            loadFolders()
        }.onFailure { error ->
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка сохранения"))
        }
    }

    fun dismissError() = intent {
        reduce { state.copy(errorMessage = null) }
    }
}
