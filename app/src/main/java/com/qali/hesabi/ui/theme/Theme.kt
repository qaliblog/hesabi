package com.qali.hesabi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF00695C),
    onPrimary = Color.White,
    secondary = Color(0xFF26A69A),
    onSecondary = Color.White,
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF263238),
    surface = Color.White,
    onSurface = Color(0xFF263238),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF26A69A),
    onPrimary = Color.Black,
    secondary = Color(0xFF80CBC4),
    onSecondary = Color.Black,
    background = Color(0xFF263238),
    onBackground = Color.White,
    surface = Color(0xFF37474F),
    onSurface = Color.White,
    error = Color(0xFFEF9A9A),
    onError = Color.Black
)

@Composable
fun HesabiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}