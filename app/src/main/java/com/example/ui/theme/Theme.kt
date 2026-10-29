package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDeep,
    primaryContainer = NavySurfaceElevated,
    onPrimaryContainer = GoldLight,
    secondary = GoldMetallic,
    onSecondary = NavyDeep,
    secondaryContainer = NavySurface,
    onSecondaryContainer = GoldLight,
    tertiary = LaserCyan,
    onTertiary = NavyDeep,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to stunning dark blue & gold theme
    dynamicColor: Boolean = false, // Keep branded DJ theme consistent
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
