package org.example.saved

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.saved.ui.navigation.AppNavigation
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.example.saved.ui.theme.SavedAppTheme

@Composable
fun App() {
    SavedAppTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) {
                AppNavigation()
            }
        }
    }
}
