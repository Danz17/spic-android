package com.henrikherzig.playintegritychecker.ui.safetynet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henrikherzig.playintegritychecker.attestation.safetynet.SafetyNetStatement
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.ui.*

/**
 * SafetyNet page - UI for SafetyNet attestation checks
 */
@Composable
fun SafetyNet(
  safetyNetResult: State<ResponseType<SafetyNetStatement>>,
  onSafetyNetRequest: () -> Unit,
  selectedIndexCheck: String,
  itemsCheck: List<List<String>>,
  changedCheck: (String) -> Unit,
  selectedIndexNonce: String,
  itemsNonce: List<List<String>>,
  changedNonce: (String) -> Unit,
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Deprecation warning card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
      )
    ) {
      Text(
        text = "SafetyNet is deprecated. Use Play Integrity API instead.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier.padding(12.dp)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Request Settings Card
    CustomElevatedCard {
      RequestSettings(
        selectedIndexCheck,
        itemsCheck,
        changedCheck,
        selectedIndexNonce,
        itemsNonce,
        changedNonce
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Make Request Button
    CustomButton(
      onClick = onSafetyNetRequest,
      icon = Icons.Outlined.GppGood,
      text = stringResource(id = R.string.safetyNet_attestation_button)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Result Card (animated)
    val showResult = safetyNetResult.value != ResponseType.None

    AnimatedVisibility(
      visible = showResult,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      CustomElevatedCard {
        Box(modifier = Modifier.animateContentSize()) {
          ResultContent(safetyNetResult) { }
        }
      }
    }

    // Bottom spacing for scroll
    Spacer(modifier = Modifier.height(80.dp))
  }
}
