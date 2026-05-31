package org.example.saved.presentation.home

import androidx.lifecycle.ViewModel
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.domain.usecase.AnalyzeUrlUseCase
import org.example.saved.domain.usecase.CreateFolderUseCase
import org.example.saved.domain.usecase.DeleteBookmarkUseCase
import org.example.saved.domain.usecase.DeleteFolderUseCase
import org.example.saved.domain.usecase.GetFoldersUseCase
import org.example.saved.domain.usecase.GetProfileUseCase
import org.example.saved.domain.usecase.GetRecentBookmarksUseCase
import org.example.saved.domain.usecase.RenameFolderUseCase
import org.example.saved.domain.usecase.SaveAnalyzedBookmarkUseCase
import org.example.saved.domain.usecase.UpdateBookmarkUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

private const val HOME_FOLDERS_LIMIT = 5

class HomeViewModel(
    private val getFoldersUseCase: GetFoldersUseCase,
    private val getRecentBookmarksUseCase: GetRecentBookmarksUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val analyzeUrlUseCase: AnalyzeUrlUseCase,
    private val saveAnalyzedBookmarkUseCase: SaveAnalyzedBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val renameFolderUseCase: RenameFolderUseCase,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> =
        container(HomeState()) {
            loadHomeData()
        }

    fun refresh() = intent {
        reduce { state.copy(isFoldersLoading = true, isBookmarksLoading = true, errorMessage = null) }
        loadHomeData()
    }

    private fun loadHomeData() = intent {
        loadFolders()
        loadRecentBookmarks()
        loadProfile()
    }

    private fun loadProfile() = intent {
        getProfileUseCase().onSuccess { profile ->
            reduce { state.copy(username = profile.username) }
        }
    }

    private fun loadFolders() = intent {
        reduce { state.copy(isFoldersLoading = true) }

        getFoldersUseCase(page = 1, limit = HOME_FOLDERS_LIMIT).onSuccess { folders ->
            reduce { state.copy(folders = folders, isFoldersLoading = false) }
        }.onFailure { error ->
            reduce { state.copy(isFoldersLoading = false, errorMessage = error.message) }
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка загрузки папок"))
        }
    }

    private fun loadRecentBookmarks() = intent {
        reduce { state.copy(isBookmarksLoading = true) }

        getRecentBookmarksUseCase().onSuccess { bookmarks ->
            reduce { state.copy(recentBookmarks = bookmarks, isBookmarksLoading = false) }
        }.onFailure { error ->
            reduce { state.copy(isBookmarksLoading = false, errorMessage = error.message) }
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка загрузки закладок"))
        }
    }

    fun createFolder(name: String) = intent {
        if (name.isBlank()) return@intent
        createFolderUseCase(name).onSuccess {
            loadHomeData()
        }.onFailure { error ->
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка создания папки"))
        }
    }

    fun openBookmark(url: String) = intent {
        postSideEffect(HomeSideEffect.OpenUrl(url))
    }

    fun analyzeUrl(url: String) = intent {
        if (url.isBlank()) return@intent
        reduce { state.copy(isAnalyzing = true) }

        analyzeUrlUseCase(url).onSuccess { result ->
            when {
                result.suggestedFolder == null -> {
                    reduce { state.copy(isAnalyzing = false) }
                    postSideEffect(HomeSideEffect.ShowError("Нейросеть не смогла определить папку — выберите вручную"))
                }
                result.isNewFolder -> {
                    reduce { state.copy(isAnalyzing = false) }
                    postSideEffect(HomeSideEffect.RequireFolderSelection(
                        url = result.url,
                        suggestedFolderName = result.suggestedFolder,
                        bookmarkTitle = result.title
                    ))
                }
                else -> {
                    saveAnalyzedBookmarkUseCase(url).onSuccess {
                        reduce { state.copy(isAnalyzing = false) }
                        loadHomeData()
                    }.onFailure { error ->
                        reduce { state.copy(isAnalyzing = false) }
                        postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка сохранения"))
                    }
                }
            }
        }.onFailure { error ->
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка анализа ссылки"))
        }
    }

    fun saveToNewFolder(url: String, folderName: String, bookmarkTitle: String) = intent {
        reduce { state.copy(isAnalyzing = true) }
        createFolderUseCase(folderName).onSuccess { folder ->
            saveAnalyzedBookmarkUseCase(url, targetFolderId = folder.id, bookmarkTitle = bookmarkTitle).onSuccess {
                reduce { state.copy(isAnalyzing = false) }
                loadHomeData()
            }.onFailure { error ->
                reduce { state.copy(isAnalyzing = false) }
                postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка сохранения"))
            }
        }.onFailure { error ->
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка создания папки"))
        }
    }

    fun saveToExistingFolder(url: String, folderId: String) = intent {
        reduce { state.copy(isAnalyzing = true) }
        saveAnalyzedBookmarkUseCase(url, targetFolderId = folderId).onSuccess {
            reduce { state.copy(isAnalyzing = false) }
            loadHomeData()
        }.onFailure { error ->
            reduce { state.copy(isAnalyzing = false) }
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка сохранения"))
        }
    }

    // ── Bookmark: delete ────────────────────────────────────────────────────

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
            loadHomeData()
        }.onFailure { error ->
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка удаления закладки"))
        }
    }

    // ── Bookmark: move ──────────────────────────────────────────────────────

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
            loadHomeData()
        }.onFailure { error ->
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка перемещения закладки"))
        }
    }

    // ── Folder: delete ───────────────────────────────────────────────────────

    fun requestDeleteFolder(folder: Folder) = intent {
        reduce { state.copy(folderPendingDelete = folder) }
    }

    fun dismissDeleteFolder() = intent {
        reduce { state.copy(folderPendingDelete = null) }
    }

    fun confirmDeleteFolder() = intent {
        val folder = state.folderPendingDelete ?: return@intent
        reduce { state.copy(folderPendingDelete = null) }
        deleteFolderUseCase(folder.id).onSuccess {
            loadHomeData()
        }.onFailure { error ->
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка удаления папки"))
        }
    }

    // ── Folder: rename ───────────────────────────────────────────────────────

    fun requestRenameFolder(folder: Folder) = intent {
        reduce { state.copy(folderPendingRename = folder) }
    }

    fun dismissRenameFolder() = intent {
        reduce { state.copy(folderPendingRename = null) }
    }

    fun confirmRenameFolder(newName: String) = intent {
        val folder = state.folderPendingRename ?: return@intent
        if (newName.isBlank()) return@intent
        reduce { state.copy(folderPendingRename = null) }
        renameFolderUseCase(folder.id, newName).onSuccess {
            loadHomeData()
        }.onFailure { error ->
            postSideEffect(HomeSideEffect.ShowError(error.message ?: "Ошибка переименования папки"))
        }
    }
}
