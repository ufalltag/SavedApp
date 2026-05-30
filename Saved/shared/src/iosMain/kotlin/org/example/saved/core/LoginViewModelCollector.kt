package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.auth.LoginSideEffect
import org.example.saved.presentation.auth.LoginState
import org.example.saved.presentation.auth.LoginViewModel

// Мост для iOS: обходит ограничение SKIE с типизацией StateFlow через Orbit Container.
// Подписываемся на flow в Kotlin и отдаём значения в обычные Swift-замыкания.
class LoginViewModelCollector(private val viewModel: LoginViewModel) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: LoginState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (LoginState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (LoginSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun dispose() = scope.cancel()
}
