package com.exercisedb.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E6B3C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB1F2B8),
    onPrimaryContainer = Color(0xFF002109),
    secondary = Color(0xFF506351),
    tertiary = Color(0xFF39656C),
    background = Color(0xFFF8FBF5),
    surface = Color(0xFFF8FBF5),
    surfaceVariant = Color(0xFFDFE5DB),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF95D69D),
    onPrimary = Color(0xFF003914),
    primaryContainer = Color(0xFF125226),
    onPrimaryContainer = Color(0xFFB1F2B8),
    secondary = Color(0xFFB7CCB7),
    tertiary = Color(0xFFA0CED6),
    background = Color(0xFF101510),
    surface = Color(0xFF101510),
)

@Immutable
data class ExtendedColors(val favorite: Color)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(favorite = Color(0xFFD32F2F))
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

@Composable
fun ExerciseDbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    val extendedColors = ExtendedColors(
        favorite = if (darkTheme) Color(0xFFFF6B6B) else Color(0xFFD32F2F),
    )
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
    }
}
