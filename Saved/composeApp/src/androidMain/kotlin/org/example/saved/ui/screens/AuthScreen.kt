package org.example.saved.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.example.saved.presentation.auth.AuthSideEffect
import org.example.saved.presentation.auth.AuthViewModel
import org.example.saved.ui.theme.LocalSnackbarHostState

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    // Локальное состояние для текста
    var emailValue by remember { mutableStateOf(TextFieldValue(state.email)) }
    var passwordValue by remember { mutableStateOf(TextFieldValue(state.password)) }

    LaunchedEffect(state.email) {
        if (state.email != emailValue.text) {
            emailValue = emailValue.copy(text = state.email)
        }
    }
    LaunchedEffect(state.password) {
        if (state.password != passwordValue.text) {
            passwordValue = passwordValue.copy(text = state.password)
        }
    }

    // Обработка Side Effects (ошибки, навигация)
    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is AuthSideEffect.NavigateToHome -> onNavigateToHome()
                is AuthSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is AuthSideEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (state.isLoginMode) "Вход в Saved" else "Регистрация",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = emailValue,
                onValueChange = {
                    emailValue = it
                    viewModel.onEmailChanged(it.text)
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordValue,
                onValueChange = {
                    passwordValue = it
                    viewModel.onPasswordChanged(it.text)
                },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submit() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp),
                    )
                } else {
                    Text(text = if (state.isLoginMode) "Войти" else "Зарегистрироваться")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { viewModel.toggleMode() },
                enabled = !state.isLoading,
            ) {
                Text(
                    text = if (state.isLoginMode) "Нет аккаунта? Создать" else "Уже есть аккаунт? Войти",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
    }
}
