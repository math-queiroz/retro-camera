package com.mathqueiroz.retrocamera.ui.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.main.MainActivity
import com.mathqueiroz.retrocamera.ui.RetroCameraTheme
import kotlin.system.exitProcess

class PermissionsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    checkAndRequestPermissions()
  }

  companion object {
    val CAMERAX_PERMISSIONS = arrayOf(
      Manifest.permission.CAMERA
    )
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    if (permissions.entries.all { it.value }) {
      startCamera()
    } else {
      handlePermissionDenied()
    }
  }

  private fun checkAndRequestPermissions() {
    val missingPermissions = CAMERAX_PERMISSIONS.filter {
      ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isEmpty()) {
      startCamera()
    } else {
      requestPermissionLauncher.launch(missingPermissions.toTypedArray())
    }
  }

  private fun handlePermissionDenied() {
    val shouldShowRationale = CAMERAX_PERMISSIONS.any {
      ActivityCompat.shouldShowRequestPermissionRationale(this, it)
    }

    if (!shouldShowRationale) {
      showSettingsDialog()
    }
  }

  private fun startCamera() {
    this.startActivity(Intent(this, MainActivity::class.java))
  }

  private fun showSettingsDialog() {
    setContent {
      PermissionSettingsDialog(
        onDismiss = { exitProcess(0) },
        onGoToSettings = {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", applicationContext.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          }
          this.startActivity(intent)
        }
      )
    }
  }

  @Composable
  fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
  ) {
    RetroCameraTheme {
      AlertDialog(
        onDismissRequest = onDismiss,
        title = {
          Text(getString(R.string.permission_request_title))
        },
        text = {
          Text(getString(R.string.permission_request_body))
        },
        confirmButton = {
          TextButton(onClick = onGoToSettings) {
            Text(getString(R.string.permission_request_go_to_settings))
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(getString(R.string.permission_request_dismiss))
          }
        }
      )
    }
  }
}