package com.mathqueiroz.retrocamera.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.mathqueiroz.retrocamera.navigation.AppNavigation
import com.mathqueiroz.retrocamera.ui.RetroCameraTheme
import com.mathqueiroz.retrocamera.ui.permissions.PermissionsActivity
import com.mathqueiroz.retrocamera.ui.permissions.PermissionsActivity.Companion.CAMERAX_PERMISSIONS

//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.initialization.InitializationStatus
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//import com.mathqueiroz.retrocamera.BuildConfig

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)

    if (!hasRequiredPermissions()) {
      this.startActivity(Intent(this, PermissionsActivity::class.java))
    }

    setContent {
      RetroCameraTheme {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
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
}

object AppConstants {
  const val MEDIA_ALBUM_NAME = "Retro_Camera"
  const val CONTACT_MAIL = "retrocameraapp.proton.me"
  const val SUPPORT_MAIL = "retrocameraapp.proton.me"
}