package com.mathqueiroz.retrocamera

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

class Util {
  companion object {
    fun findActivity(context: Context): Activity? = when (context) {
      is Activity -> context
      is ContextWrapper -> findActivity(context.baseContext)
      else -> null
    }
  }
}