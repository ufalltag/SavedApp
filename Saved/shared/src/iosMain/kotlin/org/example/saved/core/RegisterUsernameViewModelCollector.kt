package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.auth.RegisterUsernameSideEffect
import org.example.saved.presentation.auth.RegisterUsernameState
import org.example.saved.presentation.auth.RegisterUsernameViewModel

// Мост для iOS (см. комментарий в LoginViewModelCollector).
class RegisterUsernameViewModelCollector(
    private val viewModel: RegisterUsernameViewModel
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: RegisterUsernameState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (RegisterUsernameState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (RegisterUsernameSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun dispose() = scope.cancel()
}
