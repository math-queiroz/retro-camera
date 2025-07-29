package com.mathqueiroz.retrocamera.ui.main.component

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mathqueiroz.retrocamera.ui.main.MainViewModel
import com.mathqueiroz.retrocamera.ui.settings.SettingsViewModel

@Composable
fun CameraPreview(
  controller: LifecycleCameraController,
  modifier: Modifier,
  viewModel: MainViewModel
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val settings = viewModel<SettingsViewModel>()

  val flashTrigger by viewModel.flashTrigger.collectAsStateWithLifecycle()
  val showAssistiveGrid by settings.showAssistiveGrid.collectAsStateWithLifecycle()

  LaunchedEffect(showAssistiveGrid) { }

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

    BlackFlashEffect(
      triggerFlash = flashTrigger,
      onFlashComplete = { viewModel.flashCompleted() },
      modifier = Modifier
        .matchParentSize()
    )

    if (showAssistiveGrid) {
      AssistiveGrid()
    }
  }
}

@Composable
fun AssistiveGrid() {
  Canvas(
    modifier = Modifier
      .fillMaxSize()
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