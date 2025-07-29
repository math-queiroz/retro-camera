package com.mathqueiroz.retrocamera.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mathqueiroz.retrocamera.ui.cameraroll.CameraRollScreen
import com.mathqueiroz.retrocamera.ui.main.MainScreen
import com.mathqueiroz.retrocamera.ui.photopreview.PhotoPreviewScreen
import com.mathqueiroz.retrocamera.ui.settings.SettingsScreen
import com.mathqueiroz.retrocamera.ui.settings.items.AboutSettingsScreen
import com.mathqueiroz.retrocamera.ui.settings.items.PrivacySettingsScreen
import com.mathqueiroz.retrocamera.ui.settings.items.TermsSettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
  NavHost(navController, startDestination = "main") {
    composable("main") {
      MainScreen(navController)
    }
    composable(
      "cameraRoll",
      enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)) },
      exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) }
    ) {
      CameraRollScreen(navController)
    }
    composable(
      "preview/{uri}",
      arguments = listOf(navArgument("uri") { type = NavType.StringType })
    ) { backStackEntry ->
      PhotoPreviewScreen(navController, backStackEntry)
    }
    composable(
      "settings",
      enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200)) },
      popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200)) }
    ) {
      SettingsScreen(navController)
    }
    composable(
      "settings/about",
      enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200)) },
      popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200)) }
    ) {
      AboutSettingsScreen(navController)
    }
    composable(
      "settings/privacy",
      enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200)) },
      popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200)) }
    ) {
      PrivacySettingsScreen(navController)
    }
    composable(
      "settings/terms_of_use",
      enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200)) },
      popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200)) }
    ) {
      TermsSettingsScreen(navController)
    }
  }
}