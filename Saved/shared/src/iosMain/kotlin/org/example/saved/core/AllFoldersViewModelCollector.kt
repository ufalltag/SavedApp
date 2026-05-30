package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.folders.AllFoldersSideEffect
import org.example.saved.presentation.folders.AllFoldersState
import org.example.saved.presentation.folders.AllFoldersViewModel

class AllFoldersViewModelCollector(private val viewModel: AllFoldersViewModel) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: AllFoldersState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (AllFoldersState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (AllFoldersSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun loadMore() = viewModel.loadMore()

    fun dispose() = scope.cancel()
}
