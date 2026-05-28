package org.example.saved.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = AccentBlue,
        background = BackgroundLight,
        surface = SurfaceLight,
        onPrimary = White,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
    )

@Composable
fun SavedAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content,
    )
}
