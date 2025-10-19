package com.mathqueiroz.retrocamera.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mathqueiroz.retrocamera.ui.cameraroll.CameraRollScreen
import com.mathqueiroz.retrocamera.ui.main.MainScreen
import com.mathqueiroz.retrocamera.ui.main.MainViewModel
import com.mathqueiroz.retrocamera.ui.photopreview.PhotoPreviewScreen
import com.mathqueiroz.retrocamera.ui.settings.SettingsScreen
import com.mathqueiroz.retrocamera.ui.settings.SettingsViewModel
import com.mathqueiroz.retrocamera.ui.settings.items.AboutSettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
  val viewModel = viewModel<MainViewModel>()
  val settings = viewModel<SettingsViewModel>()

  NavHost(navController, startDestination = "main") {
    composable("main") {
      MainScreen(navController, viewModel, settings)
    }
    composable(
      "cameraRoll",
      enterTransition = {
        slideInVertically(initialOffsetY = { it }, animationSpec = tween(200))
      },
      exitTransition = {
        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200))
      }
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
      enterTransition = {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200))
      },
      popExitTransition = {
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
      }
    ) {
      SettingsScreen(navController, settings)
    }
    composable(
      "settings/about",
      enterTransition = {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(200))
      },
      popExitTransition = {
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
      }
    ) {
      AboutSettingsScreen(navController)
    }
  }
}