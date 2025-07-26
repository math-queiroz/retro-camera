package com.mathqueiroz.retrocamera.ui.settings

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application)  {
  private val prefs = application.applicationContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

  private val _renderFilmGrain = MutableStateFlow(
    prefs.getBoolean("renderFilmGrain", false)
  )
  val renderFilmGrain: StateFlow<Boolean> = _renderFilmGrain.asStateFlow()
  fun setRenderFilmGrain(enabled: Boolean) {
    prefs.edit { putBoolean("renderFilmGrain", enabled) }
    _renderFilmGrain.value = enabled
  }

  private val _renderTimestamp = MutableStateFlow(
    prefs.getBoolean("renderTimestamp", true)
  )
  val renderTimestamp: StateFlow<Boolean> = _renderTimestamp.asStateFlow()
  fun setRenderTimestamp(enabled: Boolean) {
    prefs.edit { putBoolean("renderTimestamp", enabled) }
    _renderTimestamp.value = enabled
  }

  private val _saveOriginalPhoto = MutableStateFlow(
    prefs.getBoolean("saveOriginalPhoto", false)
  )
  val saveOriginalPhoto: StateFlow<Boolean> = _saveOriginalPhoto.asStateFlow()
  fun setSaveOriginalPhoto(enabled: Boolean) {
    prefs.edit { putBoolean("saveOriginalPhoto", enabled) }
    _saveOriginalPhoto.value = enabled
  }
}