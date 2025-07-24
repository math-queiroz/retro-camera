package com.mathqueiroz.retrocamera.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.mathqueiroz.retrocamera.R

@Composable
fun PhotoPreviewScreen(
  navController: NavController,
  backStackEntry: NavBackStackEntry
) {
  val context = LocalContext.current
  val uri = backStackEntry.arguments?.getString("uri")?.toUri()
  uri?.let { safeUri ->
    Surface {
      Box(modifier = Modifier.fillMaxSize()) {
        Image(
          painter = rememberAsyncImagePainter(uri),
          contentDescription = null,
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        )

        IconButton(
          onClick = {
            navController.popBackStack()
          },
          modifier = Modifier
            .align(Alignment.TopStart)
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = getString(context, R.string.tip_back)
          )
        }

        Row (
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(60.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              shareImage(context, safeUri)
            },
            modifier = Modifier
              .padding(16.dp)
          ) {
            Icon(Icons.Default.IosShare, contentDescription = getString(context, R.string.tip_delete))
          }

          IconButton(
            onClick = {
              deletePhoto(context, safeUri)
              navController.popBackStack()
            },
            modifier = Modifier
              .padding(16.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = getString(context, R.string.tip_delete))
          }
        }
      }
    }
  }
}

private fun deletePhoto(context: Context, uri: Uri): Boolean {
  return try {
    val rowsDeleted = context.contentResolver.delete(uri, null, null)
    rowsDeleted > 0
  } catch (e: SecurityException) {
    e.printStackTrace()
    false
  }
}

private fun shareImage(context: Context, uri: Uri) {
  val shareIntent = Intent(Intent.ACTION_SEND).apply {
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  shareIntent.type = "image/*"
  context.startActivity(Intent.createChooser(shareIntent, getString(context, R.string.tip_share)))
}
