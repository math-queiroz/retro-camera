package com.mathqueiroz.retrocamera.ui.settings.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.component.ScreenHeaderComponent

@Composable
fun AboutSettingsScreen(
  navController: NavController
) {
  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {
      ScreenHeaderComponent(
        navController,
        getString(LocalContext.current, R.string.settings_about)
      )

      Column (
        modifier = Modifier
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_launcher_foreground),
          contentDescription = "App icon",
          modifier = Modifier.size(128.dp)
        )

        Text(
          text = "Retro Camera",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(
          modifier = Modifier
            .height(16.dp)
        )
      }

      Text("Content here")
    }
  }
}