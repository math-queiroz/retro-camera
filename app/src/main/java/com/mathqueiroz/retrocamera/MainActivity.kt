package com.mathqueiroz.retrocamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.net.Uri
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mathqueiroz.retrocamera.ui.theme.RetroCameraTheme
import kotlinx.coroutines.launch
import com.mathqueiroz.retrocamera.ui.component.CameraPreview
import com.mathqueiroz.retrocamera.ui.screen.CameraRollGridScreen
import com.mathqueiroz.retrocamera.ui.screen.MainScreen
import com.mathqueiroz.retrocamera.ui.screen.PhotoPreviewScreen
import com.mathqueiroz.retrocamera.ui.screen.SettingsScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // TODO: add better permission handling in here
    if (!hasRequiredPermissions()) {
      ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, 0)
    }

    setContent {
      RetroCameraTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "main") {
          composable("main") {
            MainScreen(navController)
          }
          composable(
            "settings",
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) }
          ) {
            SettingsScreen(navController)
          }
          composable(
            "cameraRoll",
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) }
          ) {
            CameraRollGridScreen(navController)
          }
          composable(
            "preview/{uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
          ) { backStackEntry ->
            PhotoPreviewScreen(navController, backStackEntry)
          }
        }
      }
    }
  }

  private fun hasRequiredPermissions(): Boolean {
    return CAMERAX_PERMISSIONS.all {
      ContextCompat.checkSelfPermission(
        applicationContext,
        it
      ) == PackageManager.PERMISSION_GRANTED
    }
  }

  companion object {
    private val CAMERAX_PERMISSIONS = arrayOf(
      Manifest.permission.CAMERA,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE
    )
  }
}

object AppConstants {
  const val MEDIA_ALBUM_NAME = "Retro_Camera"
}