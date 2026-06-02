package org.example.saved.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.saved.presentation.account.AccountSideEffect
import org.example.saved.presentation.account.AccountState
import org.example.saved.presentation.account.AccountViewModel

class AccountViewModelCollector(
    private val viewModel: AccountViewModel,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val currentState: AccountState
        get() = viewModel.container.stateFlow.value

    fun observeState(onChange: (AccountState) -> Unit) {
        viewModel.container.stateFlow
            .onEach { onChange(it) }
            .launchIn(scope)
    }

    fun observeSideEffects(onEffect: (AccountSideEffect) -> Unit) {
        viewModel.container.sideEffectFlow
            .onEach { onEffect(it) }
            .launchIn(scope)
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) = viewModel.changePassword(oldPassword, newPassword)

    fun logout() = viewModel.logout()

    fun dispose() = scope.cancel()
}
