package com.mathqueiroz.retrocamera.ui

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFFFFFFF),
  secondary = Color(0xFFFFFFFF),
  tertiary = Color(0xFFFFC107),

  background = Color(0xFF000000),
  surface = Color(0xFF000000),
)

private val LightColorScheme = lightColorScheme(
  secondary = Color(0xFFFFFFFF),
  primary = Color(0xFF000000),
  tertiary = Color(0xFFFFC107),

  background = Color(0xFFFFFFFF),
  surface = Color(0xFFFFFFFF),

  /* extra colors
  surface = Color(0xFF000000),
  background = Color(0x00000000),
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF1C1B1F),
  onSurface = Color(0xFF1C1B1F),
  */
)

val Typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  )
  /* Other default text styles to override
  titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  )
  */
)

@Composable
fun RetroCameraTheme(
  //darkTheme: Boolean = isSystemInDarkTheme(),
  darkTheme: Boolean = true, // force darkTheme cuz it's just better for camera apps
  dynamicColor: Boolean = false, // only available on Android 12+
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // if Android 15+
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
          val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
          view.setBackgroundColor(colorScheme.background.toArgb())
          view.setPadding(0, statusBarInsets.top, 0, 0)
          insets
        }
      } else { // if Android 14 and below
        @Suppress("DEPRECATION")
        window.statusBarColor = colorScheme.background.toArgb()
      }

      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}