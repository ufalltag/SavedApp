package org.example.saved.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.R
import org.example.saved.domain.analytics.AnalyticsTracker
import org.example.saved.presentation.auth.LoginSideEffect
import org.example.saved.presentation.auth.LoginViewModel
import org.example.saved.ui.screens.auth.components.AuthBrandHeader
import org.example.saved.ui.screens.auth.components.AuthField
import org.example.saved.ui.screens.auth.components.AuthPrimaryButton
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    analyticsTracker: AnalyticsTracker = koinInject(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var emailText by remember(state.email) { mutableStateOf(state.email) }
    var passwordText by remember(state.password) { mutableStateOf(state.password) }

    LaunchedEffect(Unit) {
        analyticsTracker.logScreen("launch_login")
    }

    LaunchedEffect(Unit) {
        viewModel.viewSideEffects.collect { effect ->
            when (effect) {
                is LoginSideEffect.NavigateToHome -> onNavigateToHome()
                is LoginSideEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is LoginSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthBrandHeader(
                title = stringResource(R.string.auth_login_title),
                subtitle = stringResource(R.string.auth_login_subtitle),
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthField(
                value = emailText,
                onValueChange = { newEmail ->
                    emailText = newEmail
                    viewModel.onEmailChanged(newEmail)
                },
                label = stringResource(R.string.auth_email_label),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthField(
                value = passwordText,
                onValueChange = { newPassword ->
                    passwordText = newPassword
                    viewModel.onPasswordChanged(newPassword)
                },
                label = stringResource(R.string.auth_password_label),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthPrimaryButton(
                text = stringResource(R.string.auth_login_button),
                onClick = { viewModel.submit() },
                isLoading = state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.auth_register_prompt_button))
            }
        }
    }
}
