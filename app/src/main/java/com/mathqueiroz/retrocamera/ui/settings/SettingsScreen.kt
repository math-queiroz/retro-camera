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
  val context = LocalContext.current

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
        SettingsButton(getString(context, R.string.settings_about), {}, isFirst = true)
        SettingsButton(getString(context, R.string.settings_report_bug), {}, isLast = true)

        SettingsButton(getString(context, R.string.settings_save_original_photo), {}, isFirst = true)
        SettingsButton(getString(context, R.string.settings_embed_location), {})
        SettingsButton(getString(context, R.string.settings_show_grid), {}, isLast = true)

        SettingsButton(getString(context, R.string.settings_share_app), {}, isFirst = true)
        SettingsButton(getString(context, R.string.settings_review), {})
        SettingsButton(getString(context, R.string.settings_contact), {}, isLast = true)

        SettingsButton(getString(context, R.string.settings_privacy), {}, isFirst = true)
        SettingsButton(getString(context, R.string.settings_terms_of_use), {}, isLast = true)
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