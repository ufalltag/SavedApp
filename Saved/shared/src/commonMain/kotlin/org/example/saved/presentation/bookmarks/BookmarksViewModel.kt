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

    /**
     * Создает новую папку.
     *
     * Вызывать из формы создания папки (например, при подтверждении в алерт-диалоге).
     */
    fun createFolder(name: String) = intent {
        if (name.isBlank()) {
            postSideEffect(BookmarksSideEffect.ShowToast("Имя папки не может быть пустым"))
            return@intent
        }

        // Опционально: можно добавить стейт isFolderCreating, если хочется блокировать UI

        repository.createFolder(name).onSuccess { newFolder ->
            postSideEffect(BookmarksSideEffect.ShowToast("Папка '$name' создана"))

            // Вариант 1: Просто перезапрашиваем папки с сервера
            // loadFolders()

            // Вариант 2: Локально добавляем папку в стейт (быстрее для UI)
            val updatedFolders = state.folders + newFolder
            reduce {
                state.copy(
                    folders = updatedFolders,
                    // Опционально: сразу выбираем свежесозданную папку
                    // selectedFolderId = newFolder.id
                )
            }
        }.onFailure { error ->
            postSideEffect(BookmarksSideEffect.ShowToast(error.message ?: "Ошибка создания папки"))
        }
    }

    fun dismissError() = intent {
        reduce { state.copy(errorMessage = null) }
    }
}
