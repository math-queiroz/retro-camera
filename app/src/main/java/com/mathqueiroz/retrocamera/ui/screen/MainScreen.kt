package com.mathqueiroz.retrocamera.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.OrientationEventListener
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.MainViewModel
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.component.CameraPreview

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MainScreen(
  navController: NavController
) {
  val context = LocalContext.current
  val activity = context.findActivity()
  activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

  val viewModel = viewModel<MainViewModel>()
  val aspectRatio = 9f/16

  var deviceRotation by remember { mutableFloatStateOf(0f) }
  DisposableEffect(Unit) {
    val orientationListener = object : OrientationEventListener(context) {
      override fun onOrientationChanged(orientation: Int) {
        if (orientation == ORIENTATION_UNKNOWN) return

        val newRotation = when {
          orientation >= 315 || orientation < 45 -> 0f      // Portrait
          orientation >= 225 && orientation < 315 -> 90f   // Landscape left
          orientation >= 135 && orientation < 225 -> 180f   // Portrait upside down
          orientation >= 45 && orientation < 135 -> 270f     // Landscape right
          else -> deviceRotation
        }

        if (newRotation != deviceRotation) {
          deviceRotation = newRotation
        }
      }
    }

    if (orientationListener.canDetectOrientation()) {
      orientationListener.enable()
    }

    onDispose {
      orientationListener.disable()
    }
  }

  val controller = remember {
    LifecycleCameraController(context).apply {
      setEnabledUseCases(
        CameraController.IMAGE_CAPTURE
      )
    }
  }

  var isCapturing by remember { mutableStateOf(false) }

  val flashState by viewModel.flashState.collectAsStateWithLifecycle()
  val (flashIcon, flashDescription) = when (flashState) {
    ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff to getString(context, R.string.tip_flash_on)
    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn to getString(context, R.string.tip_flash_off)
    else -> Icons.Default.FlashAuto to getString(context, R.string.tip_flash_on)
  }
  LaunchedEffect(flashState) {
    controller.imageCaptureFlashMode = flashState
  }

  val cameraSelector by viewModel.cameraSelector.collectAsStateWithLifecycle()
  LaunchedEffect(cameraSelector) {
    controller.cameraSelector = cameraSelector
  }

  Surface {
    Column {
      Row (
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .defaultMinSize(minHeight = 32.dp),
        horizontalArrangement = Arrangement.End,
      ) {

        if (cameraSelector != CameraSelector.DEFAULT_FRONT_CAMERA) {
          RotatingIcon(
            icon = flashIcon,
            description = flashDescription,
            rotation = deviceRotation,
            size = 32.dp,
            onClick = {
              controller.imageCaptureFlashMode = when (controller.imageCaptureFlashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
              }
              viewModel.setFlashState(controller.imageCaptureFlashMode)
            }
          )
        }
      }

      Box(
        modifier = Modifier.fillMaxSize()
      ) {
        CameraPreview(
          controller = controller,
          viewModel = viewModel,
          modifier = Modifier
            .aspectRatio(aspectRatio)
        )

        if (isCapturing) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(color = Color.White)
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp, 100.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          RotatingIcon(
            icon = Icons.Default.CameraRoll,
            description = getString(context, R.string.tip_gallery),
            rotation = deviceRotation,
            modifier = Modifier.size(70.dp),
            enabled = !isCapturing,
            onClick = {
              navController.navigate("cameraRoll")
            }
          )

          IconButton(
            modifier = Modifier.size(85.dp),
            enabled = !isCapturing,
            onClick = {
              isCapturing = true
              viewModel.TriggerBlackFlash()
              takePhoto(
                context = context,
                controller = controller,
                onPhotoTaken = { context, bitmap ->
                  isCapturing = false
                  val uri = (viewModel::onTakePhoto)(context, bitmap)
                  if (uri !== null) navController.navigate(
                    "preview/${Uri.encode(uri.toString())}"
                  )
                }
              )
            }
          ) {
            Icon(
              imageVector = Icons.Default.Circle,
              contentDescription = getString(context, R.string.tip_take_photo),
              modifier = Modifier.fillMaxSize()
            )
          }

          RotatingIcon(
            icon = Icons.Default.Cameraswitch,
            description = getString(context, R.string.tip_rotate_camera),
            rotation = deviceRotation,
            modifier = Modifier
              .size(70.dp),
            enabled = !isCapturing,
            onClick = {
              controller.cameraSelector = when (controller.cameraSelector) {
                CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> CameraSelector.DEFAULT_BACK_CAMERA
              }
              viewModel.setCameraSelector(controller.cameraSelector)
            }
          )
        }
      }
    }
  }
}

fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

@Composable
fun RotatingIcon(
  rotation: Float,
  icon: ImageVector,
  description: String,
  enabled: Boolean = true,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  tint: Color = Color.White
) {
  val animatedRotation by animateFloatAsState(
    targetValue = rotation,
    animationSpec = tween(
      durationMillis = 300,
      easing = FastOutSlowInEasing
    ),
    label = "icon_rotation"
  )

  IconButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .size(size)
      .rotate(animatedRotation)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = description,
      tint = tint,
      modifier = Modifier.size(size * 0.6f)
    )
  }
}

private fun takePhoto(
  context: Context,
  controller: LifecycleCameraController,
  onPhotoTaken: (Context, Bitmap) -> Unit
) {
  controller.takePicture(
    ContextCompat.getMainExecutor(context),
    object : OnImageCapturedCallback() {
      override fun onCaptureSuccess(image: ImageProxy) {
        super.onCaptureSuccess(image)

        val matrix = Matrix().apply {
          postRotate(image.imageInfo.rotationDegrees.toFloat())
        }

        val rotatedBitmap = Bitmap.createBitmap(
          image.toBitmap(),
          0,
          0,
          image.width,
          image.height,
          matrix,
          true
        )

        onPhotoTaken(context, rotatedBitmap)
      }

      override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        Log.e("Camera", "Couldn't take photo: ", exception)
      }
    }
  )
}
