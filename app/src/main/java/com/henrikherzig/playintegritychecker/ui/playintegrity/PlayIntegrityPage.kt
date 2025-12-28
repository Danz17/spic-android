package com.henrikherzig.playintegritychecker.ui.playintegrity

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.core.content.ContextCompat
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.attestation.PlayIntegrityStatement
import com.henrikherzig.playintegritychecker.ui.*

/**
 * Play Integrity page - main UI for integrity checks
 */
@Composable
fun PlayIntegrity(
  playIntegrityResult: State<ResponseType<PlayIntegrityStatement>>,
  onPlayIntegrityRequest: () -> Unit,
  selectedIndexCheck: String,
  itemsCheck: List<List<String>>,
  changedCheck: (String) -> Unit,
  selectedIndexNonce: String,
  itemsNonce: List<List<String>>,
  changedNonce: (String) -> Unit,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  fun openLinkExternal(url: String) {
    runCatching {
      ContextCompat.startActivity(
        context,
        Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)),
        null
      )
    }
  }

  fun openLink(url: String) {
    CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
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
      onClick = onPlayIntegrityRequest,
      icon = Icons.Outlined.GppGood,
      text = stringResource(id = R.string.playIntegrity_button)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Result Card (animated)
    val showResult = playIntegrityResult.value != ResponseType.None

    AnimatedVisibility(
      visible = showResult,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      CustomElevatedCard {
        Box(modifier = Modifier.animateContentSize()) {
          ResultContent(playIntegrityResult) { }
        }
      }
    }

    // Bottom spacing for scroll
    Spacer(modifier = Modifier.height(80.dp))
  }
}
