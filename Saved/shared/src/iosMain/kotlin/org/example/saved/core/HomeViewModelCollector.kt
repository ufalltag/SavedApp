package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.presentation.home.HomeSideEffect
import org.example.saved.presentation.home.HomeState
import org.example.saved.presentation.home.HomeViewModel

class HomeViewModelCollector(
    private val viewModel: HomeViewModel,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: HomeState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (HomeState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (HomeSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun refresh() = viewModel.refresh()

    fun openBookmark(url: String) = viewModel.openBookmark(url)

    fun analyzeUrl(url: String) = viewModel.analyzeUrl(url)

    fun createFolder(name: String) = viewModel.createFolder(name)

    fun saveToNewFolder(
        url: String,
        folderName: String,
    ) = viewModel.saveToNewFolder(url, folderName)

    fun saveToExistingFolder(
        url: String,
        folderId: String,
    ) = viewModel.saveToExistingFolder(url, folderId)

    // Bookmark: delete
    fun requestDeleteBookmark(bookmark: Bookmark) = viewModel.requestDeleteBookmark(bookmark)

    fun dismissDeleteBookmark() = viewModel.dismissDeleteBookmark()

    fun confirmDeleteBookmark() = viewModel.confirmDeleteBookmark()

    // Bookmark: move
    fun requestMoveBookmark(bookmark: Bookmark) = viewModel.requestMoveBookmark(bookmark)

    fun dismissMoveBookmark() = viewModel.dismissMoveBookmark()

    fun confirmMoveBookmark(targetFolderId: String) = viewModel.confirmMoveBookmark(targetFolderId)

    // Folder: delete
    fun requestDeleteFolder(folder: Folder) = viewModel.requestDeleteFolder(folder)

    fun dismissDeleteFolder() = viewModel.dismissDeleteFolder()

    fun confirmDeleteFolder() = viewModel.confirmDeleteFolder()

    // Folder: rename
    fun requestRenameFolder(folder: Folder) = viewModel.requestRenameFolder(folder)

    fun dismissRenameFolder() = viewModel.dismissRenameFolder()

    fun confirmRenameFolder(newName: String) = viewModel.confirmRenameFolder(newName)

    fun dispose() = scope.cancel()
}
