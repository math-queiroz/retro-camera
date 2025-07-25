package com.mathqueiroz.retrocamera.ui.component

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mathqueiroz.retrocamera.MainViewModel

@Composable
fun CameraPreview(
  controller: LifecycleCameraController,
  modifier: Modifier,
  viewModel: MainViewModel
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val flashTrigger by viewModel.flashTrigger.collectAsState()

  Box(
    modifier = modifier
  ) {
    AndroidView(
      factory = { context ->
        PreviewView(context).apply {
          scaleType = PreviewView.ScaleType.FILL_CENTER
          implementationMode = PreviewView.ImplementationMode.COMPATIBLE
          this.controller = controller
          controller.bindToLifecycle(lifecycleOwner)
        }
      },
      modifier = modifier
        .fillMaxWidth()
        .clip(RectangleShape),
      update = { previewView ->
        previewView.requestLayout()
        previewView.invalidate()
        previewView.post {
          previewView.requestLayout()
        }
      },
    )

    FlashEffect(
      triggerFlash = flashTrigger,
      onFlashComplete = { viewModel.flashCompleted() },
      modifier = Modifier
        .matchParentSize()
    )

    Canvas(
      modifier = Modifier
        .matchParentSize()
    ) {
      val numColumns = 2
      val numRows = 2

      val columnSpacing = size.width / (numColumns + 1)
      val rowSpacing = size.height / (numRows + 1)

      val paint = Paint().apply {
        color = Color.White.copy(alpha = 0.5f)
        strokeWidth = 2f
      }

      for (i in 1..numColumns) {
        val x = columnSpacing * i
        drawLine(
          color = paint.color,
          start = Offset(x, 0f),
          end = Offset(x, size.height),
          strokeWidth = paint.strokeWidth
        )
      }

      for (i in 1..numRows) {
        val y = rowSpacing * i
        drawLine(
          color = paint.color,
          start = Offset(0f, y),
          end = Offset(size.width, y),
          strokeWidth = paint.strokeWidth
        )
      }
    }
  }
}