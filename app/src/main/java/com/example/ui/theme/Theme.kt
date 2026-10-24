package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.ThemePreference

private val QuoteBloomLightColorScheme = lightColorScheme(
    primary = EditorialViolet,
    onPrimary = WarmIvory,
    primaryContainer = EditorialVioletLight,
    onPrimaryContainer = DeepPlum,
    secondary = MutedSage,
    onSecondary = WarmIvory,
    secondaryContainer = MutedSageSurface,
    onSecondaryContainer = Color(0xFF1F352A),
    tertiary = RestrainedCoral,
    onTertiary = WarmIvory,
    tertiaryContainer = RestrainedCoralSoft,
    onTertiaryContainer = Color(0xFF5C221B),
    background = WarmIvory,
    onBackground = DeepPlum,
    surface = WarmIvoryElevated,
    onSurface = DeepPlum,
    surfaceVariant = WarmIvorySurface,
    onSurfaceVariant = InkMutedLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFEDE6DC)
)

private val QuoteBloomDarkColorScheme = darkColorScheme(
    primary = SoftVioletAccent,
    onPrimary = DeepPlumDarkBg,
    primaryContainer = Color(0xFF47348A),
    onPrimaryContainer = WarmIvory,
    secondary = Color(0xFF8FB3A0),
    onSecondary = Color(0xFF13261C),
    secondaryContainer = Color(0xFF284236),
    onSecondaryContainer = Color(0xFFD9EBE1),
    tertiary = RestrainedCoral,
    onTertiary = DeepPlumDarkBg,
    tertiaryContainer = Color(0xFF6B2D24),
    onTertiaryContainer = RestrainedCoralSoft,
    background = DeepPlumDarkBg,
    onBackground = WarmIvory,
    surface = DeepPlum,
    onSurface = WarmIvory,
    surfaceVariant = DeepPlumSurface,
    onSurfaceVariant = InkMutedDark,
    outline = BorderDark,
    outlineVariant = Color(0xFF392E4D)
)

@Composable
fun QuoteBloomTheme(
    themePreference: ThemePreference = ThemePreference.LIGHT,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themePreference) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) {
        QuoteBloomDarkColorScheme
    } else {
        QuoteBloomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
