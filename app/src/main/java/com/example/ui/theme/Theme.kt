package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = EmeraldLight,
    secondary = CyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = CyanContainerDark,
    onSecondaryContainer = CyanLight,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = ObsidianDarkBg,
    onBackground = TextDarkPrimary,
    surface = SlateDarkSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = SlateDarkCard,
    onSurfaceVariant = TextDarkSecondary,
    outline = SlateDarkBorder,
    error = RedstoneRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = EmeraldDark,
    secondary = CyanDark,
    onSecondary = Color.White,
    secondaryContainer = CyanContainerLight,
    onSecondaryContainer = CyanDark,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = CanvasLightBg,
    onBackground = TextLightPrimary,
    surface = SurfaceLight,
    onSurface = TextLightPrimary,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextLightSecondary,
    outline = BorderLight,
    error = RedstoneRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep CraftHost custom brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // CraftHost default to premium dark gaming theme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
