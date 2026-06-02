package org.example.saved.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.example.saved.R
import org.example.saved.presentation.account.AccountSideEffect
import org.example.saved.presentation.account.AccountViewModel
import org.example.saved.ui.screens.account.AccountActionCards
import org.example.saved.ui.screens.account.ChangePasswordDialog
import org.example.saved.ui.screens.account.ProfileHeader
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    // Стейт видимости диалога живет здесь, так как управляет наличием узла в дереве
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is AccountSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is AccountSideEffect.LoggedOut -> {
                    onNavigateToLogin()
                }

                is AccountSideEffect.PasswordChanged -> {
                    showPasswordDialog = false
                    snackbarHostState.showSnackbar("Пароль успешно изменен")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аккаунт") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(Res.drawable.ic_arrow_back), "Назад")
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProfileHeader(email = state.email)
                    AccountActionCards(
                        onChangePasswordClick = { showPasswordDialog = true },
                        onLogoutClick = viewModel::logout,
                        isDarkMode = isDarkMode,
                        onThemeToggle = onThemeToggle,
                    )
                }
            }
        }

        if (showPasswordDialog) {
            ChangePasswordDialog(
                isChanging = state.isChangingPassword,
                onDismiss = { showPasswordDialog = false },
                onConfirm = { old, new -> viewModel.changePassword(old, new) },
            )
        }
    }
}
