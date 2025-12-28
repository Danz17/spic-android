package com.henrikherzig.playintegritychecker.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.ui.CustomButton
import com.henrikherzig.playintegritychecker.ui.CustomElevatedCard
import com.henrikherzig.playintegritychecker.ui.theme.IntegrityColors
import com.henrikherzig.playintegritychecker.widget.WidgetStateManager
import com.henrikherzig.playintegritychecker.widget.WidgetStateManager.OverallStatus

@Composable
fun Dashboard(
  onCheckNow: () -> Unit
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  val integrityState by WidgetStateManager.getIntegrityStateFlow(context)
    .collectAsState(initial = WidgetStateManager.IntegrityState())

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Main Status Card
    StatusCard(integrityState)

    Spacer(modifier = Modifier.height(16.dp))

    // Verdict Details Card
    if (integrityState.lastCheckTimestamp > 0) {
      VerdictDetailsCard(integrityState)
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Check Now Button
    CustomButton(
      onClick = onCheckNow,
      icon = Icons.Outlined.Refresh,
      text = stringResource(R.string.dashboard_checkNow)
    )

    // Last check time
    if (integrityState.lastCheckTimestamp > 0) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "${stringResource(R.string.dashboard_lastCheck)}: ${
          WidgetStateManager.formatRelativeTime(integrityState.lastCheckTimestamp)
        }",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
private fun StatusCard(state: WidgetStateManager.IntegrityState) {
  val status = state.overallStatus
  val statusColor by animateColorAsState(
    targetValue = when (status) {
      OverallStatus.STRONG -> IntegrityColors.strong
      OverallStatus.DEVICE -> IntegrityColors.device
      OverallStatus.BASIC -> IntegrityColors.basic
      OverallStatus.FAILED -> IntegrityColors.none
      OverallStatus.ERROR -> IntegrityColors.none
      OverallStatus.UNKNOWN -> MaterialTheme.colorScheme.outline
    },
    animationSpec = tween(300),
    label = "status_color"
  )

  val statusText = when (status) {
    OverallStatus.STRONG -> "MEETS_STRONG_INTEGRITY"
    OverallStatus.DEVICE -> "MEETS_DEVICE_INTEGRITY"
    OverallStatus.BASIC -> "MEETS_BASIC_INTEGRITY"
    OverallStatus.FAILED -> "NO_INTEGRITY"
    OverallStatus.ERROR -> stringResource(R.string.dashboard_error)
    OverallStatus.UNKNOWN -> stringResource(R.string.dashboard_unknown)
  }

  val dotCount = when (status) {
    OverallStatus.STRONG -> 3
    OverallStatus.DEVICE -> 2
    OverallStatus.BASIC -> 1
    else -> 0
  }

  CustomElevatedCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.dashboard_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Three-dot indicator
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 1..3) {
          Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (i <= dotCount) statusColor else MaterialTheme.colorScheme.outlineVariant
          )
          if (i < 3) Spacer(modifier = Modifier.width(8.dp))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = statusText,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = statusColor
      )

      if (status == OverallStatus.UNKNOWN) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.dashboard_tapToCheck),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (status == OverallStatus.ERROR && state.errorMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = state.errorMessage,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error
        )
      }
    }
  }
}

@Composable
private fun VerdictDetailsCard(state: WidgetStateManager.IntegrityState) {
  CustomElevatedCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Device Integrity Verdicts
      VerdictItem(
        label = stringResource(R.string.dashboard_basicIntegrity),
        passed = state.hasBasicIntegrity,
        show = state.isSuccess
      )
      VerdictItem(
        label = stringResource(R.string.dashboard_deviceIntegrity),
        passed = state.hasDeviceIntegrity,
        show = state.isSuccess
      )
      VerdictItem(
        label = stringResource(R.string.dashboard_strongIntegrity),
        passed = state.hasStrongIntegrity,
        show = state.isSuccess
      )

      if (state.appVerdict != null) {
        HorizontalDivider(
          modifier = Modifier.padding(vertical = 8.dp),
          color = MaterialTheme.colorScheme.outlineVariant
        )
        VerdictItem(
          label = stringResource(R.string.dashboard_appRecognized),
          passed = state.appVerdict == "PLAY_RECOGNIZED",
          show = true
        )
      }

      if (state.licensingVerdict != null) {
        VerdictItem(
          label = stringResource(R.string.dashboard_licensed),
          passed = state.licensingVerdict == "LICENSED",
          show = true
        )
      }
    }
  }
}

@Composable
private fun VerdictItem(
  label: String,
  passed: Boolean,
  show: Boolean
) {
  if (!show) return

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val color = if (passed) IntegrityColors.strong else IntegrityColors.none
    val icon = if (passed) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel

    Icon(
      imageVector = icon,
      contentDescription = null,
      modifier = Modifier.size(20.dp),
      tint = color
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.weight(1f))
    Text(
      text = if (passed) "PASSED" else "FAILED",
      style = MaterialTheme.typography.labelMedium,
      color = color
    )
  }
}
