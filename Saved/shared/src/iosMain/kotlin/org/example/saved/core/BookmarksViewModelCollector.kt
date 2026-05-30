package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.bookmarks.BookmarksSideEffect
import org.example.saved.presentation.bookmarks.BookmarksState
import org.example.saved.presentation.bookmarks.BookmarksViewModel

class BookmarksViewModelCollector(private val viewModel: BookmarksViewModel) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: BookmarksState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (BookmarksState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (BookmarksSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun dispose() = scope.cancel()
}
