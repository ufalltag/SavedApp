package org.example.saved.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.R
import org.example.saved.presentation.auth.RegisterUsernameSideEffect
import org.example.saved.presentation.auth.RegisterUsernameViewModel
import org.example.saved.ui.screens.auth.components.AuthBrandHeader
import org.example.saved.ui.screens.auth.components.AuthField
import org.example.saved.ui.screens.auth.components.AuthPrimaryButton
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
    var usernameText by remember(state.username) { mutableStateOf(state.username) }

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
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.auth_back_description),
                        )
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
                    .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthBrandHeader(
                title = stringResource(R.string.auth_register_username_title),
                subtitle = stringResource(R.string.auth_register_step_2_title), // e.g. "Step 2: Choose a username"
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthField(
                value = usernameText,
                onValueChange = { newUsername ->
                    usernameText = newUsername
                    viewModel.onUsernameChanged(newUsername)
                },
                label = stringResource(R.string.auth_username_label),
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthPrimaryButton(
                text = stringResource(R.string.auth_complete_registration_button),
                onClick = { viewModel.submit() },
                isLoading = state.isLoading,
            )
        }
    }
}
