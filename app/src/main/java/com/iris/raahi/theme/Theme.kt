package com.iris.raahi.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val RaahiNavy = Color(0xFF050B18)
val RaahiSecondary = Color(0xFF0B1730)
val RaahiCard = Color(0xFF102342)
val RaahiBlue = Color(0xFF1677FF)
val RaahiCyan = Color(0xFF00D9FF)
val RaahiWhite = Color(0xFFFFFFFF)
val RaahiMuted = Color(0xFFA9BED6)
val RaahiGreen = Color(0xFF42D392)
val RaahiRed = Color(0xFFFF4D67)

private val DarkColors = darkColorScheme(
    primary = RaahiBlue,
    onPrimary = RaahiWhite,
    background = RaahiNavy,
    onBackground = RaahiWhite,
    surface = RaahiCard,
    onSurface = RaahiWhite,
    surfaceVariant = RaahiSecondary,
    onSurfaceVariant = RaahiMuted,
    secondary = RaahiCyan,
    onSecondary = RaahiNavy,
    tertiary = RaahiGreen,
    error = RaahiRed
)

private val LightColors = lightColorScheme(
    primary = RaahiBlue,
    onPrimary = RaahiWhite,
    background = Color(0xFFF4F8FF),
    onBackground = RaahiSecondary,
    surface = RaahiWhite,
    onSurface = RaahiSecondary
)

@Composable
fun RAAHITheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkScheme = if (highContrast) DarkColors.copy(primary = Color(0xFF63A4FF)) else DarkColors
    MaterialTheme(
        colorScheme = darkScheme,
        typography = androidx.compose.material3.Typography(
            headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
            titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            bodyLarge = TextStyle(fontSize = 18.sp),
            bodyMedium = TextStyle(fontSize = 16.sp),
            labelLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        ),
        shapes = androidx.compose.material3.Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp)
        ),
        content = content
    )
}
