package com.henrikherzig.playintegritychecker.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.henrikherzig.playintegritychecker.MainActivity
import com.henrikherzig.playintegritychecker.R

/**
 * Manages notifications for integrity status changes
 */
object IntegrityNotificationManager {

  private const val CHANNEL_ID = "integrity_alerts"
  private const val CHANNEL_NAME = "Integrity Alerts"
  private const val CHANNEL_DESCRIPTION = "Notifications for Play Integrity status changes"

  private const val NOTIFICATION_ID_VERDICT_CHANGE = 1001
  private const val NOTIFICATION_ID_CHECK_FAILED = 1002

  /**
   * Create notification channel (required for Android 8.0+)
   */
  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESCRIPTION
        enableVibration(true)
        setShowBadge(true)
      }

      val notificationManager = context.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)
    }
  }

  /**
   * Show notification when integrity verdict changes
   */
  fun showVerdictChangeNotification(
    context: Context,
    title: String,
    message: String,
    isImprovement: Boolean
  ) {
    createNotificationChannel(context)

    // Create intent to open app
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Choose icon based on whether it's an improvement or degradation
    val icon = if (isImprovement) {
      android.R.drawable.ic_dialog_info
    } else {
      android.R.drawable.ic_dialog_alert
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(icon)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setCategory(NotificationCompat.CATEGORY_STATUS)
      .build()

    // Check notification permission for Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_VERDICT_CHANGE, notification)
  }

  /**
   * Show notification when integrity check fails
   */
  fun showCheckFailedNotification(context: Context, errorMessage: String) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_alert)
      .setContentTitle("Integrity Check Failed")
      .setContentText(errorMessage)
      .setStyle(NotificationCompat.BigTextStyle().bigText(errorMessage))
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setCategory(NotificationCompat.CATEGORY_ERROR)
      .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CHECK_FAILED, notification)
  }

  /**
   * Cancel all notifications
   */
  fun cancelAllNotifications(context: Context) {
    NotificationManagerCompat.from(context).cancelAll()
  }

  /**
   * Check if notification permission is granted
   */
  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }
}
