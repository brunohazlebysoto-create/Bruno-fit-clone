package com.brunofit.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = LimePrimary,
    onPrimary = BgDark,
    secondary = CyanAccent,
    onSecondary = BgDark,
    background = BgDark,
    onBackground = InkLight,
    surface = PanelDark,
    onSurface = InkLight,
    surfaceVariant = PanelDark2,
    onSurfaceVariant = MutedGreen,
    outline = LineDark,
    error = RoseAccent,
    onError = BgDark,
)

@Composable
fun BrunoFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = BrunoFitTypography,
        content = content
    )
}
