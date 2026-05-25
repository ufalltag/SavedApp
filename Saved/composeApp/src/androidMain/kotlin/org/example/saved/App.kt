package org.example.saved

import androidx.compose.runtime.Composable
import org.example.saved.ui.navigation.AppNavigation
import org.example.saved.ui.theme.SavedAppTheme

@Composable
fun App() {
    SavedAppTheme {
        AppNavigation()
    }
}
