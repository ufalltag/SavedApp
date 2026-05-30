package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.domain.model.Bookmark
import org.example.saved.presentation.folderlinks.FolderLinksSideEffect
import org.example.saved.presentation.folderlinks.FolderLinksState
import org.example.saved.presentation.folderlinks.FolderLinksViewModel

class FolderLinksViewModelCollector(private val viewModel: FolderLinksViewModel) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: FolderLinksState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (FolderLinksState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (FolderLinksSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun loadMore() = viewModel.loadMore()
    fun requestDeleteBookmark(bookmark: Bookmark) = viewModel.requestDeleteBookmark(bookmark)
    fun dismissDeleteBookmark() = viewModel.dismissDeleteBookmark()
    fun confirmDeleteBookmark() = viewModel.confirmDeleteBookmark()
    fun requestMoveBookmark(bookmark: Bookmark) = viewModel.requestMoveBookmark(bookmark)
    fun dismissMoveBookmark() = viewModel.dismissMoveBookmark()
    fun confirmMoveBookmark(targetFolderId: String) = viewModel.confirmMoveBookmark(targetFolderId)

    fun dispose() = scope.cancel()
}
