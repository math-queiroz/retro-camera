package com.mathqueiroz.retrocamera.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.component.ScreenHeaderComponent

@Composable
fun SettingsScreen(navController: NavController) {
  Surface {
    Column (
      modifier = Modifier
        .fillMaxSize()
    ) {
      ScreenHeaderComponent(
        navController,
        getString(LocalContext.current, R.string.term_settings)
      )

      Column (
        modifier = Modifier
          .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(0.dp)
      ) {
        SettingsButton("About", {}, isFirst = true)
        SettingsButton("Report a Bug", {}, isLast = true)

        SettingsButton("Save Original Photo", {}, isFirst = true)
        SettingsButton("Embed Location", {})
        SettingsButton("Show Grid", {}, isLast = true)

        SettingsButton("Share the App", {}, isFirst = true)
        SettingsButton("Leave a Review", {})
        SettingsButton("Contact", {}, isLast = true)

        SettingsButton("Privacy", {}, isFirst = true)
        SettingsButton("Terms of Use", {}, isLast = true)
      }
    }
  }
}

@Composable
fun SettingsButton(
  text: String,
  onClick: () -> Unit,
  isFirst: Boolean = false,
  isLast: Boolean = false
) {
  val rounding = 26.dp
  val shape = when {
    isFirst && isLast -> RoundedCornerShape(rounding)
    isFirst -> RoundedCornerShape(topStart = rounding, topEnd = rounding)
    isLast -> RoundedCornerShape(bottomStart = rounding, bottomEnd = rounding)
    else -> RectangleShape
  }

  val innerPadding = 18.dp
  val sectionPadding = 18.dp
  val padding = when {
    isLast -> PaddingValues(bottom = sectionPadding)
    else -> PaddingValues(0.dp)
  }

  Surface(
    onClick = onClick,
    shape = shape,
    color = Color(0xff1b1b1b),
    modifier = Modifier
      .fillMaxWidth()
      .padding(padding)
  ) {
    Column {
      if (!isFirst) {
        Divider(
          modifier = Modifier
            .padding(horizontal = innerPadding)
        )
      }
      Text(
        text,
        modifier = Modifier
          .padding(innerPadding)
      )
    }
  }
}