package org.example.saved.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.presentation.auth.RegisterUsernameSideEffect
import org.example.saved.presentation.auth.RegisterUsernameViewModel
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUsernameScreen(
    viewModel: RegisterUsernameViewModel,
    onNavigateToLogin: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val state by viewModel.viewStates.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.viewSideEffects.collect { effect ->
            when (effect) {
                is RegisterUsernameSideEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is RegisterUsernameSideEffect.NavigateToLogin -> onNavigateToLogin(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Имя пользователя") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painter = painterResource(Res.drawable.ic_arrow_back), contentDescription = "Назад")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Шаг 2: Как вас называть?", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.onUsernameChanged(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submit() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                enabled = !state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Завершить регистрацию")
                }
            }
        }
    }
}
