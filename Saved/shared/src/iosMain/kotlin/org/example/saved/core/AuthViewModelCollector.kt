package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.auth.AuthSideEffect
import org.example.saved.presentation.auth.AuthState
import org.example.saved.presentation.auth.AuthViewModel

// Вспомогательный класс для iOS: обходит ограничение SKIE,
// который не может правильно затипизировать StateFlow<T> через ObjC bridge
// когда тип идёт через Orbit Container.
// Решение: подписываемся на flow в Kotlin и передаём значения через обычные Swift-замыкания.
class AuthViewModelCollector(private val viewModel: AuthViewModel) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Синхронный доступ к текущему состоянию — SKIE правильно типизирует
    // конкретный возвращаемый тип AuthState
    val currentState: AuthState
        get() = viewModel.container.stateFlow.value

    // Подписываемся на изменения состояния — onChange вызывается при каждом изменении
    // Kotlin lambda (AuthState) -> Unit становится Swift (AuthState) -> Void
    fun observeState(onChange: (AuthState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    // Подписываемся на side effects — разовые события (навигация, ошибки)
    fun observeSideEffects(onEffect: (AuthSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    // Вызываем когда View исчезает — отменяем все подписки
    fun dispose() = scope.cancel()
}
