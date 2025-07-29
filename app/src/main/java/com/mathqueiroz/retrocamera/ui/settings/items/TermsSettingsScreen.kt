package com.mathqueiroz.retrocamera.ui.settings.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.component.ScreenHeaderComponent

@Composable
fun TermsSettingsScreen(
  navController: NavController
) {
  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {
      ScreenHeaderComponent(
        navController,
        getString(LocalContext.current, R.string.settings_terms_of_use)
      )

      Text("Content here")
    }
  }
}