package com.mathqueiroz.retrocamera.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.MainViewModel
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.component.CameraPreview

@Composable
fun MainScreen(
  navController: NavController
) {
  val viewModel = viewModel<MainViewModel>()
  val context = LocalContext.current
  val aspectRatio = 9f/16

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
          IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
              controller.imageCaptureFlashMode = when (controller.imageCaptureFlashMode) {
                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                else -> ImageCapture.FLASH_MODE_OFF
              }
              viewModel.setFlashState(controller.imageCaptureFlashMode)
            }
          ) {
            Icon(
              imageVector = flashIcon,
              contentDescription = flashDescription,
              modifier = Modifier.size(16.dp)
            )
          }
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
          IconButton(
            modifier = Modifier.size(70.dp),
            enabled = !isCapturing,
            onClick = {
              navController.navigate("cameraRoll")
            }
          ) {
            Icon(
              imageVector = Icons.Default.CameraRoll,
              contentDescription = getString(context, R.string.tip_gallery),
              modifier = Modifier
                .background(Color(0, 0, 0, 76), shape = CircleShape)
                .padding(10.dp)
            )
          }

          IconButton(
            modifier = Modifier.size(85.dp),
            enabled = !isCapturing,
            onClick = {
              isCapturing = true
              viewModel.triggerFlash()
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

          IconButton(
            modifier = Modifier
              .size(70.dp),
            enabled = !isCapturing,
            onClick = {
              controller.cameraSelector = if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                  CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                  CameraSelector.DEFAULT_BACK_CAMERA
                }
              viewModel.setCameraSelector(controller.cameraSelector)
            }
          ) {
            Icon(
              imageVector = Icons.Default.Cameraswitch,
              contentDescription = getString(context, R.string.tip_rotate_camera),
              modifier = Modifier
                .background(Color(0, 0, 0, 76), shape = CircleShape)
                .padding(10.dp)
            )
          }
        }
      }
    }
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
