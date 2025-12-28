package com.henrikherzig.playintegritychecker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.henrikherzig.playintegritychecker.MainActivity

/**
 * Medium (3x2) widget showing detailed integrity status
 */
class IntegrityGlanceWidgetMedium : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val state = WidgetStateManager.getIntegrityState(context)

    provideContent {
      GlanceTheme {
        MediumWidgetContent(state)
      }
    }
  }

  @Composable
  private fun MediumWidgetContent(state: WidgetStateManager.IntegrityState) {
    val cardBackground = ColorProvider(0xFFFFFFFF.toInt())
    val textPrimary = ColorProvider(0xFF212121.toInt())
    val textSecondary = ColorProvider(0xFF757575.toInt())

    Box(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(cardBackground)
        .cornerRadius(16.dp)
        .clickable(actionStartActivity<MainActivity>())
        .padding(12.dp)
    ) {
      Column(
        modifier = GlanceModifier.fillMaxSize()
      ) {
        // Header row with title and refresh button
        Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Play Integrity",
            style = TextStyle(
              color = textPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.defaultWeight()
          )

          Box(
            modifier = GlanceModifier
              .size(28.dp)
              .clickable(actionRunCallback<RefreshWidgetAction>()),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "\u21BB",
              style = TextStyle(
                color = ColorProvider(0xFF6200EE.toInt()),
                fontSize = 16.sp
              )
            )
          }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Verdict rows
        VerdictRow(
          label = "STRONG",
          isPassed = state.hasStrongIntegrity,
          isChecked = state.lastCheckTimestamp > 0
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        VerdictRow(
          label = "DEVICE",
          isPassed = state.hasDeviceIntegrity,
          isChecked = state.lastCheckTimestamp > 0
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        VerdictRow(
          label = "BASIC",
          isPassed = state.hasBasicIntegrity,
          isChecked = state.lastCheckTimestamp > 0
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        // Last check timestamp
        Text(
          text = if (state.isSuccess || state.lastCheckTimestamp > 0) {
            "Last: ${WidgetStateManager.formatRelativeTime(state.lastCheckTimestamp)}"
          } else if (state.errorMessage != null) {
            "Error: ${state.errorMessage!!.take(20)}..."
          } else {
            "Tap to check"
          },
          style = TextStyle(
            color = textSecondary,
            fontSize = 10.sp
          )
        )
      }
    }
  }

  @Composable
  private fun VerdictRow(label: String, isPassed: Boolean, isChecked: Boolean) {
    val iconColor = when {
      !isChecked -> ColorProvider(0xFF9E9E9E.toInt()) // gray
      isPassed -> ColorProvider(0xFF4CAF50.toInt()) // green
      else -> ColorProvider(0xFFF44336.toInt()) // red
    }

    val icon = when {
      !isChecked -> "\u25CB" // empty circle
      isPassed -> "\u2713" // checkmark
      else -> "\u2717" // X mark
    }

    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = icon,
        style = TextStyle(
          color = iconColor,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      )

      Spacer(modifier = GlanceModifier.width(8.dp))

      Text(
        text = label,
        style = TextStyle(
          color = if (isChecked && isPassed) {
            ColorProvider(0xFF212121.toInt())
          } else {
            ColorProvider(0xFF757575.toInt())
          },
          fontSize = 12.sp,
          fontWeight = if (isPassed) FontWeight.Medium else FontWeight.Normal
        )
      )
    }
  }
}

/**
 * Receiver for medium widget
 */
class IntegrityWidgetReceiverMedium : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = IntegrityGlanceWidgetMedium()
}
