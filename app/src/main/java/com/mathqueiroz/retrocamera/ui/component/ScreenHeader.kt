package com.mathqueiroz.retrocamera.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.R

@Composable
fun ScreenHeader(navController: NavController, title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.Top
  ) {
    IconButton(onClick = { navController.popBackStack() }) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = getString(LocalContext.current, R.string.tip_back)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge
    )
  }
}