package com.ehan.mobrush.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameDarkColorScheme = darkColorScheme(
  primary = CrimsonPrimary,
  onPrimary = Color.White,
  primaryContainer = CrimsonDark,
  onPrimaryContainer = GoldAccent,
  secondary = GoldAccent,
  onSecondary = Color.Black,
  secondaryContainer = DarkSurfaceElevated,
  onSecondaryContainer = GoldGlow,
  tertiary = CyanMana,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = DarkSurfaceBorder
)

@Composable
fun MobRushTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = GameDarkColorScheme,
    typography = Typography,
    content = content
  )
}
