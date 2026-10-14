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
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = Slate800,
  onPrimaryContainer = Color.White,
  secondary = Slate400,
  background = Slate900,
  surface = Slate800,
  onBackground = Slate100,
  onSurface = Slate100,
  outline = Slate600
)

private val LightColorScheme = lightColorScheme(
  primary = Slate800,
  onPrimary = Color.White,
  primaryContainer = Slate100,
  onPrimaryContainer = Slate900,
  secondary = Slate600,
  background = Color.White,
  surface = Color.White,
  onBackground = Slate900,
  onSurface = Slate900,
  outline = Slate300
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
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
