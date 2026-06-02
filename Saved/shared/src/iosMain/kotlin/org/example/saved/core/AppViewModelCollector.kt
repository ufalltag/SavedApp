package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.app.AppSideEffect
import org.example.saved.presentation.app.AppState
import org.example.saved.presentation.app.AppViewModel

class AppViewModelCollector(
    private val viewModel: AppViewModel,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: AppState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (AppState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (AppSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun onAuthenticated() = viewModel.onAuthenticated()

    fun onLoggedOut() = viewModel.onLoggedOut()

    fun dispose() = scope.cancel()
}
