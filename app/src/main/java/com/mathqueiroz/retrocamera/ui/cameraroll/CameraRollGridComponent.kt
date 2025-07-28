package com.mathqueiroz.retrocamera.ui.cameraroll

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import coil.compose.rememberAsyncImagePainter
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.ui.main.AppConstants

@Composable
fun CameraRollGrid(
  onPhotoClick: (Uri) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

  LaunchedEffect(Unit) {
    imageUris = loadRetroCameraThumbnails(context)
  }

  if(imageUris.isEmpty()) {
    Box(
      modifier = modifier.padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(getString(context, R.string.cameraroll_no_photos))
    }
  } else {
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      verticalArrangement = Arrangement.spacedBy(2.dp),
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      items(imageUris) { uri ->
        Image(
          painter = rememberAsyncImagePainter(uri),
          contentDescription = null,
          modifier = Modifier
            .aspectRatio(1f)
            .clickable { onPhotoClick(uri) },
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}

fun loadRetroCameraThumbnails(context: Context): List<Uri> {
  val imageUris = mutableListOf<Uri>()
  val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
  val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.RELATIVE_PATH
  )

  val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
  val selectionArgs = arrayOf("%Pictures/${AppConstants.MEDIA_ALBUM_NAME}%")

  context.contentResolver.query(
    collection,
    projection,
    selection,
    selectionArgs,
    "${MediaStore.Images.Media.DATE_ADDED} DESC"
  )?.use { cursor ->
    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

    while (cursor.moveToNext()) {
      val id = cursor.getLong(idColumn)
      val uri = ContentUris.withAppendedId(collection, id)
      imageUris.add(uri)
    }
  }

  return imageUris
}