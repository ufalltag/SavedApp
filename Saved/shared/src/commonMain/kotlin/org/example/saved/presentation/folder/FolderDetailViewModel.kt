package org.example.saved.presentation.folder

import androidx.lifecycle.ViewModel
import org.example.saved.domain.repository.BookmarkRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class FolderDetailViewModel(
    private val repository: BookmarkRepository
) : ViewModel(), ContainerHost<FolderDetailState, FolderDetailSideEffect> {

    override val container: Container<FolderDetailState, FolderDetailSideEffect> =
        container(FolderDetailState())

    // Инициализация экрана
    fun initFolder(folderId: String, folderName: String) = intent {
        // Устанавливаем базовые данные, чтобы UI мог сразу отрендерить заголовок
        reduce { state.copy(folderId = folderId, folderName = folderName, isLoading = true) }

        // Грузим закладки из сети
        repository.getBookmarks(folderId).fold(
            onSuccess = { bookmarks ->
                reduce { state.copy(bookmarks = bookmarks, isLoading = false) }
            },
            onFailure = { error ->
                reduce { state.copy(isLoading = false, errorMessage = error.message) }
                postSideEffect(FolderDetailSideEffect.ShowError(error.message ?: "Unknown error"))
            }
        )
    }

    fun onBookmarkClick(url: String) = intent {
        postSideEffect(FolderDetailSideEffect.OpenUrl(url))
    }

    fun onDeleteBookmark(bookmarkId: String) = intent {
        reduce { state.copy(isDeleting = true) }

        repository.deleteBookmark(bookmarkId).fold(
            onSuccess = {
                val updatedBookmarks = state.bookmarks.filter { it.id != bookmarkId }

                reduce {
                    state.copy(
                        bookmarks = updatedBookmarks,
                        isDeleting = false
                    )
                }
                postSideEffect(FolderDetailSideEffect.ShowMessage("Закладка удалена"))
            },
            onFailure = { error ->
                reduce { state.copy(isDeleting = false) }
                postSideEffect(FolderDetailSideEffect.ShowError(error.message ?: "Ошибка удаления"))
            }
        )
    }

}
