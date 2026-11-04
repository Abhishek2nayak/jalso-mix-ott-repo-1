package com.jalsomix.ott.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * iOS-style dark palette (true black, grouped surfaces, hairline separators)
 * with the molten-gold accent taken from the JalsomixOTT logo.
 */
object JM {
    val Bg = Color(0xFF000000)
    val Surface = Color(0xFF1C1C1E)
    val SurfaceHigh = Color(0xFF2C2C2E)
    val Outline = Color(0xFF38383A)
    val GoldLight = Color(0xFFFFE2A3)
    val Gold = Color(0xFFF5C26B)
    val GoldDeep = Color(0xFFE8A03A)
    val Ember = Color(0xFFFF5A3C)
    val Magenta = Color(0xFFD7357A)
    val Text = Color(0xFFF2F2F7)
    val Muted = Color(0xFF8E8E93)
    val OnGold = Color(0xFF1A1206)
    val Danger = Color(0xFFFF453A)

    // iOS system colours used for the Settings-style icon tiles
    val Blue = Color(0xFF0A84FF)
    val Green = Color(0xFF30D158)
    val Orange = Color(0xFFFF9F0A)
    val Purple = Color(0xFFBF5AF2)
    val Teal = Color(0xFF40C8E0)
    val Pink = Color(0xFFFF375F)
    val Indigo = Color(0xFF5E5CE6)
    val Gray = Color(0xFF636366)

    val GoldGradient = Brush.horizontalGradient(listOf(GoldLight, Gold, GoldDeep))
    val EmberGradient = Brush.linearGradient(listOf(Gold, Ember))
    val FireGradient = Brush.linearGradient(listOf(GoldDeep, Ember, Magenta))
}

private val Scheme = darkColorScheme(
    primary = JM.Gold,
    onPrimary = JM.OnGold,
    secondary = JM.Ember,
    onSecondary = Color.White,
    background = JM.Bg,
    onBackground = JM.Text,
    surface = JM.Surface,
    onSurface = JM.Text,
    surfaceVariant = JM.SurfaceHigh,
    onSurfaceVariant = JM.Muted,
    outline = JM.Outline,
    error = JM.Danger
)

private val F = FontFamily.SansSerif

private val Typo = Typography(
    displaySmall = TextStyle(fontFamily = F, fontWeight = FontWeight.Bold, fontSize = 34.sp, letterSpacing = (-0.6).sp),
    headlineMedium = TextStyle(fontFamily = F, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.4).sp),
    headlineSmall = TextStyle(fontFamily = F, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = (-0.2).sp),
    titleLarge = TextStyle(fontFamily = F, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = F, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, letterSpacing = (-0.2).sp),
    bodyLarge = TextStyle(fontFamily = F, fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp),
    bodyMedium = TextStyle(fontFamily = F, fontSize = 15.sp, lineHeight = 21.sp, letterSpacing = (-0.1).sp),
    bodySmall = TextStyle(fontFamily = F, fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = F, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    labelSmall = TextStyle(fontFamily = F, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.6.sp)
)

@Composable
fun JalsomixTheme(content: @Composable () -> Unit) {
    // The app is intentionally always dark – cinema feel.
    MaterialTheme(colorScheme = Scheme, typography = Typo, content = content)
}
