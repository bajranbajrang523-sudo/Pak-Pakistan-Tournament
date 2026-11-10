package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = PrimaryBackground,
    primaryContainer = Color(0xFF003D4C),
    onPrimaryContainer = ElectricCyanGlow,

    secondary = FireOrange,
    onSecondary = PrimaryBackground,
    secondaryContainer = Color(0xFF4D2000),
    onSecondaryContainer = FireOrangeGlow,

    tertiary = PurpleAccent,
    onTertiary = TextWhite,
    tertiaryContainer = Color(0xFF381A6F),
    onTertiaryContainer = PurpleAccentLight,

    background = PrimaryBackground,
    onBackground = TextWhite,

    surface = SecondaryBackground,
    onSurface = TextWhite,
    surfaceVariant = EsportsSurfaceVariant,
    onSurfaceVariant = TextLightGray,

    error = NeonRedDanger,
    onError = TextWhite,
    outline = SurfaceCardBorder
)

@Composable
fun ProGeniusBattleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
