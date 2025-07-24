package com.mathqueiroz.retrocamera.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mathqueiroz.retrocamera.ui.component.ScreenHeader
import kotlin.to

@Composable
fun SettingsScreen(
  navController: NavController
) {
  val cameraSettings: List<Pair<String, MutableState<Boolean>>> = listOf(
    "Save Original Photo" to remember { mutableStateOf(true) },
    "Save Location Metadata" to remember { mutableStateOf(false) },
    "Mirror Frontal Camera" to remember { mutableStateOf(false) },
    "Show Grid" to remember { mutableStateOf(false) },
    "Show Level" to remember { mutableStateOf(false) }
  )

  val section2Settings: List<Pair<String, MutableState<Boolean>>> = listOf(
    "Location Access" to remember { mutableStateOf(true) },
    "Auto Sync" to remember { mutableStateOf(false) }
  )

  Surface(
    modifier = Modifier
      .fillMaxSize()
  ) {
    ScreenHeader(navController, "Settings")
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      item { SettingsSection(title = "About", items = cameraSettings) }
      item { Spacer(modifier = Modifier.height(24.dp)) }
      item { SettingsSection(title = "Privacy", items = section2Settings) }
    }
  }
}

@Composable
fun SettingsSection(title: String, items: List<Pair<String, MutableState<Boolean>>>) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    items.forEach { (label, toggleState) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(
          checked = toggleState.value,
          onCheckedChange = { toggleState.value = it }
        )
      }
    }
  }
}
