package com.mathqueiroz.retrocamera.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.R

@Composable
fun ScreenHeaderComponent(navController: NavController, title: String) {
  Row {
    IconButton(
      onClick = {
        navController.popBackStack()
      },
      modifier = Modifier
        .align (alignment = Alignment.CenterVertically)
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = getString(LocalContext.current, R.string.tip_back),
      )
    }

    Text(
      text = title,
      fontSize = 20.sp,
      modifier = Modifier
        .padding(16.dp)
    )
  }
}