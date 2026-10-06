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

// Police Red & Blue Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PoliceBlueLight,
    onPrimary = PoliceNavy900,
    primaryContainer = PoliceNavy700,
    onPrimaryContainer = Color.White,
    secondary = PoliceRedLight,
    onSecondary = Color.White,
    secondaryContainer = PoliceRedDark,
    onSecondaryContainer = PoliceRedContainer,
    tertiary = PoliceGold,
    background = SurfaceDark,
    surface = SurfaceCardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = PoliceNavy800,
    onSurfaceVariant = TextSecondaryDark,
    error = EmergencyRed
)

// Police Red & Blue Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = PoliceNavy800,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE6F5),
    onPrimaryContainer = PoliceNavy900,
    secondary = PoliceRed,
    onSecondary = Color.White,
    secondaryContainer = PoliceRedContainer,
    onSecondaryContainer = PoliceRedDark,
    tertiary = PoliceGold,
    background = SurfaceLight,
    surface = SurfaceCardLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    error = EmergencyRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep authentic police Red & Blue palette
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
