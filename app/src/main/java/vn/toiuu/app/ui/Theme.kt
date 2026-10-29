package vn.toiuu.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF1A365D)
val NavySoft = Color(0xFF2C5282)
val Sky = Color(0xFFE8EEF6)
val Amber = Color(0xFFB7791F)
val AmberBg = Color(0xFFFFF4E5)
val AmberInk = Color(0xFF7B4B12)
val Paper = Color(0xFFF7F9FC)
val Ink = Color(0xFF1A202C)
val Muted = Color(0xFF5A6678)
val Line = Color(0xFFD9E0E8)

private val LightScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Sky,
    onPrimaryContainer = Navy,
    secondary = NavySoft,
    onSecondary = Color.White,
    tertiary = Amber,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEDF1F6),
    onSurfaceVariant = Muted,
    outline = Line,
    error = Color(0xFFB3261E)
)

@Composable
fun ToiUuTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightScheme, content = content)
}
