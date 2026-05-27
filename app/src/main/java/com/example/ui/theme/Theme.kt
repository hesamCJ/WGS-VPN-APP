package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val CyberDarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonGreen,
    tertiary = CyberPink,
    background = CosmicBg,
    surface = CosmicCard,
    onPrimary = CosmicBg,
    onSecondary = CosmicBg,
    onTertiary = CosmicBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CosmicCardElevated,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We ignore dynamic colors to preserve our tailored premium sci-fi gaming branding!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = CyberDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CosmicBg.toArgb()
            window.navigationBarColor = CosmicBg.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
