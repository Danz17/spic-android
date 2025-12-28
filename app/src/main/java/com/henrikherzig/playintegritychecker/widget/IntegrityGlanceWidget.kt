package com.henrikherzig.playintegritychecker.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.henrikherzig.playintegritychecker.MainActivity
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.background.WorkManagerScheduler

/**
 * Small (2x1) widget showing compact integrity status
 */
class IntegrityGlanceWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val state = WidgetStateManager.getIntegrityState(context)

    provideContent {
      GlanceTheme {
        SmallWidgetContent(state)
      }
    }
  }

  @Composable
  private fun SmallWidgetContent(state: WidgetStateManager.IntegrityState) {
    val backgroundColor = when (state.overallStatus) {
      WidgetStateManager.OverallStatus.STRONG -> ColorProvider(0xFF4CAF50.toInt())
      WidgetStateManager.OverallStatus.DEVICE -> ColorProvider(0xFF8BC34A.toInt())
      WidgetStateManager.OverallStatus.BASIC -> ColorProvider(0xFFFF9800.toInt())
      WidgetStateManager.OverallStatus.FAILED -> ColorProvider(0xFFF44336.toInt())
      WidgetStateManager.OverallStatus.ERROR -> ColorProvider(0xFFF44336.toInt())
      WidgetStateManager.OverallStatus.UNKNOWN -> ColorProvider(0xFF9E9E9E.toInt())
    }

    val statusText = when (state.overallStatus) {
      WidgetStateManager.OverallStatus.STRONG -> "STRONG"
      WidgetStateManager.OverallStatus.DEVICE -> "DEVICE"
      WidgetStateManager.OverallStatus.BASIC -> "BASIC"
      WidgetStateManager.OverallStatus.FAILED -> "FAILED"
      WidgetStateManager.OverallStatus.ERROR -> "ERROR"
      WidgetStateManager.OverallStatus.UNKNOWN -> "CHECK"
    }

    val statusIcon = when (state.overallStatus) {
      WidgetStateManager.OverallStatus.STRONG,
      WidgetStateManager.OverallStatus.DEVICE,
      WidgetStateManager.OverallStatus.BASIC -> "\u2713" // checkmark
      WidgetStateManager.OverallStatus.FAILED,
      WidgetStateManager.OverallStatus.ERROR -> "\u2717" // X mark
      WidgetStateManager.OverallStatus.UNKNOWN -> "?"
    }

    Box(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(backgroundColor)
        .clickable(actionStartActivity<MainActivity>())
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize()
      ) {
        // Status icon/text
        Text(
          text = "$statusIcon $statusText",
          style = TextStyle(
            color = ColorProvider(0xFFFFFFFF.toInt()),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          ),
          modifier = GlanceModifier.defaultWeight()
        )

        // Refresh button
        Box(
          modifier = GlanceModifier
            .size(32.dp)
            .clickable(actionRunCallback<RefreshWidgetAction>()),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "\u21BB", // refresh symbol
            style = TextStyle(
              color = ColorProvider(0xFFFFFFFF.toInt()),
              fontSize = 18.sp
            )
          )
        }
      }
    }
  }
}

/**
 * Receiver for small widget
 */
class IntegrityWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = IntegrityGlanceWidget()
}

/**
 * Action to refresh the widget and trigger an integrity check
 */
class RefreshWidgetAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    // Schedule immediate integrity check
    WorkManagerScheduler.scheduleImmediateCheck(context)

    // Update all widgets
    IntegrityGlanceWidget().updateAll(context)
    IntegrityGlanceWidgetMedium().updateAll(context)
  }
}
