package com.dogeby.tagplayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TagPlayerDarkPrimary,
    onPrimary = TagPlayerDarkOnPrimary,
    primaryContainer = TagPlayerDarkPrimaryContainer,
    onPrimaryContainer = TagPlayerDarkOnPrimaryContainer,
    inversePrimary = TagPlayerDarkPrimaryInverse,
    secondary = TagPlayerDarkSecondary,
    onSecondary = TagPlayerDarkOnSecondary,
    secondaryContainer = TagPlayerDarkSecondaryContainer,
    onSecondaryContainer = TagPlayerDarkOnSecondaryContainer,
    tertiary = TagPlayerDarkTertiary,
    onTertiary = TagPlayerDarkOnTertiary,
    tertiaryContainer = TagPlayerDarkTertiaryContainer,
    onTertiaryContainer = TagPlayerDarkOnTertiaryContainer,
    error = TagPlayerDarkError,
    onError = TagPlayerDarkOnError,
    errorContainer = TagPlayerDarkErrorContainer,
    onErrorContainer = TagPlayerDarkOnErrorContainer,
    background = TagPlayerDarkBackground,
    onBackground = TagPlayerDarkOnBackground,
    surface = TagPlayerDarkSurface,
    onSurface = TagPlayerDarkOnSurface,
    inverseSurface = TagPlayerDarkInverseSurface,
    inverseOnSurface = TagPlayerDarkInverseOnSurface,
    surfaceVariant = TagPlayerDarkSurfaceVariant,
    onSurfaceVariant = TagPlayerDarkOnSurfaceVariant,
    outline = TagPlayerDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = TagPlayerLightPrimary,
    onPrimary = TagPlayerLightOnPrimary,
    primaryContainer = TagPlayerLightPrimaryContainer,
    onPrimaryContainer = TagPlayerLightOnPrimaryContainer,
    inversePrimary = TagPlayerLightPrimaryInverse,
    secondary = TagPlayerLightSecondary,
    onSecondary = TagPlayerLightOnSecondary,
    secondaryContainer = TagPlayerLightSecondaryContainer,
    onSecondaryContainer = TagPlayerLightOnSecondaryContainer,
    tertiary = TagPlayerLightTertiary,
    onTertiary = TagPlayerLightOnTertiary,
    tertiaryContainer = TagPlayerLightTertiaryContainer,
    onTertiaryContainer = TagPlayerLightOnTertiaryContainer,
    error = TagPlayerLightError,
    onError = TagPlayerLightOnError,
    errorContainer = TagPlayerLightErrorContainer,
    onErrorContainer = TagPlayerLightOnErrorContainer,
    background = TagPlayerLightBackground,
    onBackground = TagPlayerLightOnBackground,
    surface = TagPlayerLightSurface,
    onSurface = TagPlayerLightOnSurface,
    inverseSurface = TagPlayerLightInverseSurface,
    inverseOnSurface = TagPlayerLightInverseOnSurface,
    surfaceVariant = TagPlayerLightSurfaceVariant,
    onSurfaceVariant = TagPlayerLightOnSurfaceVariant,
    outline = TagPlayerLightOutline
)

@Composable
fun TagPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
