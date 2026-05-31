package org.example.saved.presentation.bookmarks

import androidx.lifecycle.ViewModel
import org.example.saved.domain.usecase.CreateFolderUseCase
import org.example.saved.domain.usecase.DeleteBookmarkUseCase
import org.example.saved.domain.usecase.DeleteFolderUseCase
import org.example.saved.domain.usecase.GetBookmarksUseCase
import org.example.saved.domain.usecase.GetFoldersUseCase
import org.example.saved.domain.usecase.RenameFolderUseCase
import org.example.saved.domain.usecase.SaveAnalyzedBookmarkUseCase
import org.example.saved.domain.usecase.UpdateBookmarkUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class BookmarksViewModel(
    private val getFoldersUseCase: GetFoldersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val renameFolderUseCase: RenameFolderUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val saveAnalyzedBookmarkUseCase: SaveAnalyzedBookmarkUseCase
) : ViewModel(), ContainerHost<BookmarksState, BookmarksSideEffect> {

    override val container: Container<BookmarksState, BookmarksSideEffect> =
        container(BookmarksState()) {
            loadFolders()
        }

    private fun loadFolders() = intent {
        reduce { state.copy(isFoldersLoading = true, errorMessage = null) }

        getFoldersUseCase().onSuccess { folders ->
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

        getBookmarksUseCase(folderId).onSuccess { bookmarks ->
            reduce { state.copy(bookmarks = bookmarks, isBookmarksLoading = false) }
        }.onFailure { error ->
            reduce { state.copy(isBookmarksLoading = false, errorMessage = error.message) }
            postSideEffect(BookmarksSideEffect.ShowToast("Ошибка загрузки закладок"))
        }
    }

    fun analyzeAndSaveUrl(url: String) = intent {
        if (state.isAnalyzing) return@intent
        if (state.folders.isEmpty()) {
            postSideEffect(BookmarksSideEffect.ShowToast("Сначала создайте хотя бы одну папку"))
            return@intent
        }
        reduce { state.copy(isAnalyzing = true) }

        saveAnalyzedBookmarkUseCase(url = url).onSuccess {
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(BookmarksSideEffect.ShowToast("Закладка успешно сохранена!"))
            loadFolders()
        }.onFailure { error ->
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка сохранения"))
        }
    }

    /**
     * Создаёт новую папку и добавляет её в стейт локально (без перезапроса с сервера).
     */
    fun createFolder(name: String) = intent {
        if (name.isBlank()) {
            postSideEffect(BookmarksSideEffect.ShowToast("Имя папки не может быть пустым"))
            return@intent
        }

        createFolderUseCase(name).onSuccess { newFolder ->
            reduce { state.copy(folders = state.folders + newFolder) }
            postSideEffect(BookmarksSideEffect.ShowToast("Папка '$name' создана"))
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка создания папки"))
        }
    }

    fun openBookmark(url: String) = intent {
        postSideEffect(BookmarksSideEffect.OpenUrl(url))
    }

    fun renameFolder(folderId: String, newName: String) = intent {
        if (newName.isBlank()) {
            postSideEffect(BookmarksSideEffect.ShowToast("Имя папки не может быть пустым"))
            return@intent
        }

        renameFolderUseCase(folderId, newName).onSuccess {
            val updatedFolders = state.folders.map { folder ->
                if (folder.id == folderId) folder.copy(name = newName) else folder
            }
            reduce { state.copy(folders = updatedFolders) }
            postSideEffect(BookmarksSideEffect.ShowToast("Папка переименована"))
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка переименования папки"))
        }
    }

    fun deleteFolder(folderId: String) = intent {
        deleteFolderUseCase(folderId).onSuccess {
            val wasSelected = state.selectedFolderId == folderId
            val updatedFolders = state.folders.filter { it.id != folderId }
            val newSelectedId = if (wasSelected) updatedFolders.firstOrNull()?.id else state.selectedFolderId
            reduce {
                state.copy(
                    folders = updatedFolders,
                    selectedFolderId = newSelectedId,
                    bookmarks = if (wasSelected) emptyList() else state.bookmarks
                )
            }
            postSideEffect(BookmarksSideEffect.ShowToast("Папка удалена"))
            if (wasSelected && newSelectedId != null) {
                loadBookmarks(newSelectedId)
            }
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка удаления папки"))
        }
    }

    fun deleteBookmark(bookmarkId: String) = intent {
        deleteBookmarkUseCase(bookmarkId).onSuccess {
            reduce { state.copy(bookmarks = state.bookmarks.filter { it.id != bookmarkId }) }
            postSideEffect(BookmarksSideEffect.ShowToast("Закладка удалена"))
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка удаления закладки"))
        }
    }

    fun moveBookmark(bookmarkId: String, targetFolderId: String) = intent {
        updateBookmarkUseCase(bookmarkId, folderId = targetFolderId).onSuccess {
            reduce { state.copy(bookmarks = state.bookmarks.filter { it.id != bookmarkId }) }
            postSideEffect(BookmarksSideEffect.ShowToast("Закладка перемещена"))
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка перемещения закладки"))
        }
    }

    fun renameBookmark(bookmarkId: String, newTitle: String) = intent {
        if (newTitle.isBlank()) {
            postSideEffect(BookmarksSideEffect.ShowToast("Название не может быть пустым"))
            return@intent
        }

        updateBookmarkUseCase(bookmarkId, title = newTitle).onSuccess {
            val updatedBookmarks = state.bookmarks.map { bookmark ->
                if (bookmark.id == bookmarkId) bookmark.copy(title = newTitle) else bookmark
            }
            reduce { state.copy(bookmarks = updatedBookmarks) }
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка переименования закладки"))
        }
    }

    fun dismissError() = intent {
        reduce { state.copy(errorMessage = null) }
    }
}
