package com.mathqueiroz.retrocamera.ui.cameraroll

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
fun CameraRollScreen(navController: NavController) {
  Surface {
     Column {
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
           text = getString(LocalContext.current, R.string.camera_roll),
           fontSize = 20.sp,
           modifier = Modifier
             .padding(16.dp)
         )
       }

       CameraRollGrid(
        onPhotoClick = { uri ->
          navController.navigate("preview/${Uri.encode(uri.toString())}")
        },
        modifier = Modifier
          .fillMaxSize()
      )
    }
    Row(
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .size(75.dp)
    ) {
      IconButton(
        onClick = {
          navController.popBackStack()
        }
      ) {
        Icon(
          tint = MaterialTheme.colorScheme.background,
          imageVector = Icons.Default.CameraAlt,
          contentDescription = getString(LocalContext.current, R.string.tip_rotate_camera),
          modifier = Modifier
            .background(MaterialTheme.colorScheme.onBackground, shape = CircleShape)
            .padding(10.dp)
            .size(75.dp)
        )
      }
    }
  }
}