package com.mathqueiroz.retrocamera.ui.settings

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import com.google.android.play.core.review.ReviewManagerFactory
import com.mathqueiroz.retrocamera.R
import com.mathqueiroz.retrocamera.Util
import com.mathqueiroz.retrocamera.ui.component.ScreenHeaderComponent
import com.mathqueiroz.retrocamera.ui.main.AppConstants
import androidx.core.net.toUri

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
        SettingsButton(
          context.getString(R.string.settings_about),
          {
            navController.navigate("settings/about")
          },
          isFirst = true
        )
        SettingsButton(
          context.getString(R.string.settings_report_bug),
          {
            reportBug(context)
          },
          isLast = true
        )

        SettingsButton(
          context.getString(R.string.settings_save_original_photo),
          {},
          isFirst = true
        )
        SettingsButton(
          context.getString(R.string.settings_embed_location),
          {}
        )
        SettingsButton(
          context.getString(R.string.settings_show_grid),
          {},
          isLast = true
        )

        SettingsButton(
          context.getString(R.string.settings_share_app),
          {
            shareApp(context)
          },
          isFirst = true
        )
        SettingsButton(
          context.getString(R.string.settings_review),
          {
            Util.findActivity(context)?.let { activity ->
              requestReview(activity) {
                Log.i("Review", "App reviewed")
              }
            }
          }
        )
        SettingsButton(
          context.getString(R.string.settings_contact),
          {
            getInContact(context)
          },
          isLast = true
        )

        SettingsButton(
          context.getString(R.string.settings_privacy),
          {
            navController.navigate("settings/privacy")
          },
          isFirst = true
        )
        SettingsButton(
          context.getString(R.string.settings_terms_of_use),
          {
            navController.navigate("settings/terms_of_use")
          },
          isLast = true
        )
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

private fun requestReview(activity: Activity, onReviewed: () -> Unit) {
  val manager = ReviewManagerFactory.create(activity)
  val request = manager.requestReviewFlow()

  request.addOnCompleteListener { task ->
    if (task.isSuccessful) {
      val reviewInfo = task.result
      val flow = manager.launchReviewFlow(activity, reviewInfo)
      flow.addOnCompleteListener {
        onReviewed()
      }
    }
  }
}

private fun shareApp(context: Context) {
  val shareSubject = context.getString(R.string.share_app_subject)
  val shareMessage = context.getString(R.string.share_app_message)

  val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, shareSubject)
    putExtra(Intent.EXTRA_TEXT,
      "$shareMessage https://play.google.com/store/apps/details?id=${context.packageName}")
  }

  val chooser = Intent.createChooser(shareIntent, "Share app with friends")
  context.startActivity(chooser)
}

private fun getInContact(context: Context) {
  val contactTerm = context.getString(R.string.settings_contact)

  val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
    data = "mailto:".toUri()
    putExtra(Intent.EXTRA_EMAIL, arrayOf(AppConstants.CONTACT_MAIL))
    putExtra(Intent.EXTRA_SUBJECT,
      "$contactTerm - ${context.getString(R.string.app_name)}"
    )
  }

  try {
    context.startActivity(Intent.createChooser(emailIntent, contactTerm))
  } catch (e: ActivityNotFoundException) {
    Toast.makeText(
      context,
      context.getString(R.string.warn_no_email_app),
      Toast.LENGTH_SHORT
    ).show()
  }
}

private fun reportBug(context: Context) {
  val reportTerm = context.getString(R.string.settings_report_bug)
  val bugReportDescribe = context.getString(R.string.bug_report_describe)
  val bugReportDevice = context.getString(R.string.bug_report_device)
  val bugReportVersion = context.getString(R.string.bug_report_version)


  val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
    data = "mailto:".toUri()
    putExtra(Intent.EXTRA_EMAIL, arrayOf(AppConstants.SUPPORT_MAIL))
    putExtra(Intent.EXTRA_SUBJECT,
      "$reportTerm - ${context.getString(R.string.app_name)}"
    )
    putExtra(Intent.EXTRA_TEXT, """
           $bugReportDescribe
           
           
           $bugReportDevice: ${Build.MODEL}
           Android: ${Build.VERSION.RELEASE}
           $bugReportVersion: ${
             context.packageManager.getPackageInfo(context.packageName, 0)
               .versionName
           }
       """.trimIndent())
  }

  try {
    context.startActivity(Intent.createChooser(emailIntent, reportTerm))
  } catch (e: ActivityNotFoundException) {
    Toast.makeText(
      context,
      context.getString(R.string.warn_no_email_app),
      Toast.LENGTH_SHORT
    ).show()
  }
}