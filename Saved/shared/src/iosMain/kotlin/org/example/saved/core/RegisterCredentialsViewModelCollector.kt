package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.auth.RegisterCredentialsSideEffect
import org.example.saved.presentation.auth.RegisterCredentialsState
import org.example.saved.presentation.auth.RegisterCredentialsViewModel

// Мост для iOS (см. комментарий в LoginViewModelCollector).
class RegisterCredentialsViewModelCollector(
    private val viewModel: RegisterCredentialsViewModel
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: RegisterCredentialsState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (RegisterCredentialsState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (RegisterCredentialsSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun dispose() = scope.cancel()
}
