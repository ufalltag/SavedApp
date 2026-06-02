package org.example.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.saved.presentation.app.AppViewModel
import org.example.saved.ui.navigation.AppNavigation
import org.example.saved.ui.theme.LocalSnackbarHostState
import org.example.saved.ui.theme.SavedAppTheme
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App() {
    val appViewModel = koinViewModel<AppViewModel>()
    val state by appViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val useDarkTheme = state.isDarkMode ?: isSystemInDarkTheme()

    SavedAppTheme(darkTheme = useDarkTheme) {
        val snackbarHostState = remember { SnackbarHostState() }
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) {
                // ПАТЧ: Прокидываем инстанс вниз!
                AppNavigation(appViewModel = appViewModel)
            }
        }
    }
}
