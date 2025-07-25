package com.mathqueiroz.retrocamera.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FlashEffect(
  triggerFlash: Boolean,
  modifier: Modifier = Modifier,
  onFlashComplete: () -> Unit
) {
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(triggerFlash) {
    if (triggerFlash) {
      alpha.snapTo(1f)
      alpha.animateTo(0f, animationSpec = tween(durationMillis = 300))
      onFlashComplete()
    }
  }

  if (alpha.value > 0f) {
    Box(
      modifier = modifier
        .background(Color.Black.copy(alpha = alpha.value))
    )
  }
}