package com.mathqueiroz.retrocamera.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.view.OrientationEventListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.settings.SettingsViewModel
import com.mathqueiroz.retrocamera.ui.main.component.CameraPreview
import kotlin.math.min

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun MainScreen(
  navController: NavController
) {
  val context = LocalContext.current
  val viewModel = viewModel<MainViewModel>()
  val settings = viewModel<SettingsViewModel>()

  val activity = context.findActivity()
  activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

  var deviceRotation by remember { mutableFloatStateOf(0f) }
  DisposableEffect(Unit) {
    val orientationListener = object : OrientationEventListener(context) {
      override fun onOrientationChanged(orientation: Int) {
        if (orientation == ORIENTATION_UNKNOWN) return

        val newRotation = when {
          orientation >= 315 || orientation < 45 -> 0f      // Portrait
          // orientation >= 135 && orientation < 225 -> 180f   // Portrait upside down
          orientation >= 225 -> 90f   // Landscape left
          else -> 270f // Landscape right
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

  var isCapturing by remember { mutableStateOf(false) }
  val controller = remember {
    LifecycleCameraController(context).apply {
      setEnabledUseCases(
        CameraController.IMAGE_CAPTURE
      )
    }
  }

  val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
  val renderFilmGrain by settings.renderFilmGrain.collectAsStateWithLifecycle()
  val renderTimestamp by settings.renderTimestamp.collectAsStateWithLifecycle()

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

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    uri?.let {
      val (bitmap, exif) = getBitmapFromUri(context, it)
      bitmap?.let{ bitmap ->
        viewModel.onTakePhoto(context, bitmap, exif)?.let { uri ->
          navController.navigate(parseUriPreview(uri))
        }
      }
    }

  }

  Surface {
    Box(
      modifier = Modifier
        .padding(0.dp, 40.dp)
        .fillMaxWidth()
        .aspectRatio(min(AspectRatio.PORTRAIT_4_3.ratio, aspectRatio.ratio)),
      contentAlignment = Alignment.TopCenter
    ) {
      CameraPreview(
        controller = controller,
        viewModel = viewModel,
        modifier = Modifier
          .align(alignment = Alignment.Center)
          .aspectRatio(aspectRatio.ratio)
      )
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
    ) {
      Row (
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .defaultMinSize(minHeight = 32.dp),
        horizontalArrangement = Arrangement.End,
      ) {
        if (cameraSelector != CameraSelector.DEFAULT_FRONT_CAMERA) {
          RotatingIconButton(
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

      Column (
        modifier = Modifier
          .fillMaxWidth()
          .align(alignment = Alignment.BottomCenter),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {

        Row (
          modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding( 4.dp)
        ) {
          RotatingIconButton(
            icon = Icons.Default.AddPhotoAlternate,
            description = getString(context, R.string.term_import_photos),
            rotation = deviceRotation,
            enabled = !isCapturing,
            onClick = {
              launcher.launch("image/*")
            }
          )

          RotatingIconButton(
            icon = Icons.Default.AspectRatio,
            description = getString(LocalContext.current, R.string.tip_toggle_aspect_ratio),
            rotation = deviceRotation,
            enabled = !isCapturing,
            onClick = {
              viewModel.toggleAspectRatio()
            }
          )

          RotatingIconButton(
            icon = Icons.Default.Grain,
            description = getString(LocalContext.current, R.string.tip_toggle_film_grain),
            tint = (
              if (renderFilmGrain) MaterialTheme.colorScheme.tertiary
              else MaterialTheme.colorScheme.primary
            ),
            rotation = deviceRotation,
            enabled = !isCapturing,
            onClick = {
              settings.setRenderFilmGrain(!renderFilmGrain)
            }
          )

          RotatingIconButton(
            icon = Icons.Default.CalendarMonth,
            description = getString(LocalContext.current, R.string.tip_toggle_timestamp),
            tint = (
              if (renderTimestamp) MaterialTheme.colorScheme.tertiary
              else MaterialTheme.colorScheme.primary
            ),
            rotation = deviceRotation,
            enabled = !isCapturing,
            onClick = {
              settings.setRenderTimestamp(!renderTimestamp)
            }
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 100.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          RotatingIconButton(
            icon = Icons.Default.CameraRoll,
            description = getString(context, R.string.tip_camera_roll),
            rotation = deviceRotation,
            enabled = !isCapturing,
            modifier = Modifier
              .clip(shape = CircleShape)
              .background(Color.Black.copy(alpha = 0.2f)),
            onClick = {
              navController.navigate("cameraRoll")
            }
          )

          IconButton(
            modifier = Modifier
              .size(85.dp)
              .shadow(0.dp, CircleShape),
            enabled = !isCapturing,
            onClick = {
              isCapturing = true
              viewModel.triggerBlackFlash()
              takePhoto(
                context = context,
                controller = controller,
                aspectRatio = aspectRatio.ratio,
                onPhotoTaken = { context, bitmap ->
                  isCapturing = false
                  val uri = (viewModel::onTakePhoto)(context, bitmap, null)
                  if (uri !== null) navController.navigate(parseUriPreview(uri))
                }
              )
            }
          ) {
            Icon(
              imageVector = Icons.Default.Circle,
              contentDescription = getString(context, R.string.tip_take_photo),
              modifier = Modifier
                .fillMaxSize(),
            )
          }

          RotatingIconButton(
            icon = Icons.Default.FlipCameraAndroid,
            description = getString(context, R.string.tip_rotate_camera),
            rotation = deviceRotation,
            enabled = !isCapturing,
            modifier = Modifier
              .clip(shape = CircleShape)
              .background(Color.Black.copy(alpha = 0.2f)),
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

@Composable
fun RotatingIconButton(
  rotation: Float,
  icon: ImageVector,
  description: String,
  enabled: Boolean = true,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  tint: Color = MaterialTheme.colorScheme.primary
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
    modifier = Modifier
      .size(size)
      .then(modifier)
      .rotate(animatedRotation)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = description,
      tint = tint,
      modifier = Modifier
        .size(size * 0.6f)
    )
  }
}

private fun takePhoto(
  context: Context,
  aspectRatio: Float,
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

        val croppedBitmap = cropBitmapCenter(rotatedBitmap, aspectRatio)

        onPhotoTaken(context, croppedBitmap)
      }

      override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        Log.e("Camera", "Couldn't take photo: ", exception)
      }
    }
  )
}

fun cropBitmapCenter(bitmap: Bitmap, aspectRatio: Float): Bitmap {
  val newWidth: Int
  val newHeight: Int

  if (bitmap.width / bitmap.height.toFloat() > aspectRatio) {
    newHeight = bitmap.height
    newWidth = (bitmap.height * aspectRatio).toInt()
  } else {
    newWidth = bitmap.width
    newHeight = (bitmap.width / aspectRatio).toInt()
  }

  return Bitmap.createBitmap(bitmap,
    (bitmap.width - newWidth) / 2,
    (bitmap.height - newHeight) / 2,
    newWidth, newHeight)
}

private fun getBitmapFromUri(context: Context, uri: Uri): Pair<Bitmap?, ExifInterface?> {
  return context.contentResolver.openInputStream(uri)?.use { inputStream ->
    val bitmap = BitmapFactory.decodeStream(inputStream)
    context.contentResolver.openInputStream(uri)?.use { exifStream ->
      val exif = ExifInterface(exifStream)
      Pair(bitmap, exif)
    } ?: Pair(bitmap, null)
  } ?: Pair(null, null)
}

fun parseUriPreview(uri: Uri): String {
  return "preview/${Uri.encode(uri.toString())}"
}

fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}
