package com.mathqueiroz.retrocamera.ui.main

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BitmapCompat.createScaledBitmap
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import com.mathqueiroz.retrocamera.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter
import java.util.Date
import java.util.Random

class MainViewModel : ViewModel() {
  private val _flashTrigger = MutableStateFlow(false)
  val flashTrigger: StateFlow<Boolean> = _flashTrigger
  fun triggerBlackFlash() { _flashTrigger.value = true }
  fun flashCompleted() { _flashTrigger.value = false }

  private val _flashState = MutableStateFlow(ImageCapture.FLASH_MODE_OFF)
  val flashState: StateFlow<Int> = _flashState
  fun setFlashState(state: Int) { _flashState.value = state }

  private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
  val cameraSelector: StateFlow<CameraSelector> = _cameraSelector
  fun setCameraSelector(state: CameraSelector) { _cameraSelector.value = state }

  private val _aspectRatio = MutableStateFlow(AspectRatio.PORTRAIT_16_9)
  val aspectRatio: StateFlow<AspectRatio> = _aspectRatio
  fun toggleAspectRatio() {
    _aspectRatio.value = AspectRatio.getNext(_aspectRatio.value)
  }

  fun onTakePhoto(context: Context, bitmap: Bitmap): Uri? {
    var result = bitmap
    result = applyFilter(context, result)
    result = addFilmGrain(result)
    result = addTimestamp(context, result)
    return saveBitmapToGallery(context, result)
  }

  private fun addTimestamp(context: Context, bitmap: Bitmap): Bitmap {
    val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(result)
    val timestampFormat = "yy年 M月d日"

    val isPortrait = bitmap.height > bitmap.width
    if (isPortrait) {
      canvas.translate(bitmap.width.toFloat(), 0f)
      canvas.rotate(90f)
    }

    val paint = Paint().apply {
      color = Color(0xBEFCE46D).toArgb()
      textSize = bitmap.width.toFloat() * 0.035f
      typeface = ResourcesCompat.getFont(context, R.font.retrocamera_dotmatrix)
      textSkewX = -0.075f
      alpha = 160
      setShadowLayer(6f, 0f, 0f, Color(0xFFFFAF25).toArgb())
    }

    val timestampRect = Rect()
    paint.getTextBounds(timestampFormat, 0, timestampFormat.length, timestampRect)

    val baseWidth = if (isPortrait) bitmap.height else bitmap.width
    val baseHeight = if (isPortrait) bitmap.width else bitmap.height

    val timestampXPos = baseWidth * 0.79f - timestampRect.width()
    val timestampYPos = baseHeight * 0.9f

    val timestamp =
      SimpleDateFormat(timestampFormat, Locale.getDefault()).format(Date())
    canvas.drawText("'$timestamp", timestampXPos, timestampYPos, paint)

    return result
  }

  private fun applyFilter(context: Context, bitmap: Bitmap): Bitmap {
    val result = GPUImage(context)
    result.setImage(bitmap)
    val lutFilter = GPUImageLookupFilter()
    val lut = BitmapFactory.decodeResource(context.resources, R.drawable.cpm35)
    lutFilter.bitmap = lut

    val hazeFilter = GPUImageHazeFilter()
    hazeFilter.setDistance(-0.05f)
    hazeFilter.setSlope(0.0f)

    val filters = GPUImageFilterGroup(listOf(
      lutFilter, hazeFilter
    ))
    result.setFilter(filters)
    return result.bitmapWithFilterApplied
  }

  private fun addFilmGrain(bitmap: Bitmap): Bitmap {
    val grainScale = 3f
    val grainIntensity = 0.25f

    val width = (bitmap.width / grainScale).toInt()
    val height = (bitmap.height / grainScale).toInt()

    val config = bitmap.config ?: Bitmap.Config.ARGB_8888
    val noisyBitmap = createBitmap(width, height, config)
    val paint = Paint()

    val random = Random()

    val noisePixels = IntArray(width * height) {
      val shade = (random.nextFloat() * 255).toInt()
      Color(shade, shade, shade, (grainIntensity * 255).toInt()).toArgb()
    }

    noisyBitmap.setPixels(noisePixels, 0,width, 0, 0, width, height)
    val scaledNoisyBitmap = createScaledBitmap(
      noisyBitmap,
      bitmap.width,
      bitmap.height,
      null,
      true
    )

    val finalBitmap = createBitmap(bitmap.width, bitmap.height, config)
    val finalCanvas = Canvas(finalBitmap)

    finalCanvas.drawBitmap(bitmap, 0f, 0f, null)
    paint.alpha = (grainIntensity * 255).toInt()
    finalCanvas.drawBitmap(scaledNoisyBitmap,0f, 0f, paint)

    return finalBitmap
  }

  private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
    val filename = "IMG_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
      put(MediaStore.Images.Media.DISPLAY_NAME, filename)
      put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
      put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${AppConstants.MEDIA_ALBUM_NAME}")
    }

    val contentResolver = context.contentResolver
    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
      contentResolver.openOutputStream(uri)?.use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
      }
    }

    return imageUri
  }
}

enum class AspectRatio(val ratio: Float, val displayName: String) {
  SQUARE(1f, "1:1"),
  PORTRAIT_4_3(3f/4f, "3:4"),
  PORTRAIT_16_9(9f/16f, "9:16");

  companion object {
    fun getNext(current: AspectRatio): AspectRatio {
      val values = AspectRatio.entries.toTypedArray()
      val currentIndex = values.indexOf(current)
      return values[(currentIndex + 1) % values.size]
    }
  }
}