package com.henrikherzig.playintegritychecker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.ui.theme.IntegrityColors

/**
 * Custom Card Element - Material 3 styled
 * Used as a wrapper for API results, loading screens, or error screens
 */
@Composable
fun CustomCard(content: @Composable () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    content = { content() }
  )
}

/**
 * Elevated Card for primary content
 */
@Composable
fun CustomElevatedCard(content: @Composable () -> Unit) {
  ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    content = { content() }
  )
}

/**
 * Title Element within the Card
 */
@Composable
fun CustomCardTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.padding(bottom = 4.dp)
  )
  Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Title Element within the Card (one layer deeper than Title)
 */
@Composable
fun CustomCardTitle2(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.padding(bottom = 2.dp)
  )
  Spacer(modifier = Modifier.height(4.dp))
}

/**
 * Title Element within the Card (another layer deeper than title2)
 */
@Composable
fun CustomCardTitle3(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )
}

/**
 * Creates 2 Text Elements for label and value display
 */
@Composable
fun CustomCardGroup(text1: String, text2: String) {
  CustomCardTitle3(text1)
  Text(
    text = text2,
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurface
  )
  Spacer(modifier = Modifier.height(12.dp))
}

/**
 * Boolean status display with icon
 */
@Composable
fun CustomCardBool(text: String, passed: Boolean) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )
  Spacer(modifier = Modifier.height(2.dp))

  val result = if (passed) stringResource(R.string.sn_passed) else stringResource(R.string.sn_failed)
  val color = if (passed) IntegrityColors.strong else IntegrityColors.none
  val icon = if (passed) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel

  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      tint = color,
      contentDescription = null,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = result,
      style = MaterialTheme.typography.bodyMedium,
      color = color
    )
  }
  Spacer(modifier = Modifier.height(12.dp))
}

/**
 * Horizontal boolean status display
 */
@Composable
fun CustomCardBoolHorizontal(text: String, passed: Boolean?) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    val color = when (passed) {
      true -> IntegrityColors.strong
      false -> IntegrityColors.none
      null -> MaterialTheme.colorScheme.outline
    }
    val icon = if (passed == true) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel

    Icon(
      imageVector = icon,
      tint = color,
      contentDescription = null,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = color
    )
  }
  Spacer(modifier = Modifier.height(12.dp))
}

/**
 * Primary action button - Material 3 styled
 */
@Composable
fun CustomButton(onClick: () -> Unit, icon: ImageVector, text: String) {
  FilledTonalButton(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp),
    colors = ButtonDefaults.filledTonalButtonColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      modifier = Modifier.size(20.dp)
    )
    Spacer(Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge
    )
  }
}

/**
 * Secondary action button
 */
@Composable
fun CustomOutlinedButton(onClick: () -> Unit, icon: ImageVector, text: String) {
  OutlinedButton(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = MaterialTheme.colorScheme.primary
    )
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      modifier = Modifier.size(20.dp)
    )
    Spacer(Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge
    )
  }
}

/**
 * Alert dialog with content
 */
@Composable
fun CustomContentAlertDialog(
  modifier: Modifier = Modifier,
  titleString: String,
  content: @Composable (() -> Unit)? = null,
  opened: MutableState<Boolean>,
) {
  CustomAlertDialog(
    modifier = modifier,
    titleString = titleString,
    titleIcon = Icons.AutoMirrored.Outlined.Help,
    opened = opened,
    content = content,
  )
}

/**
 * Code display alert dialog with copy functionality
 */
@Composable
fun CustomCodeAlertDialog(
  content: String,
  opened: MutableState<Boolean>,
) {
  val localClipboardManager = LocalClipboardManager.current
  CustomAlertDialog(
    titleString = stringResource(id = R.string.dialog_title),
    titleIcon = Icons.Outlined.Code,
    opened = opened,
    content = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        SelectionContainer {
          Text(
            text = content,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
              .verticalScroll(rememberScrollState())
              .padding(12.dp)
          )
        }
      }
    },
    dismissButton = {
      TextButton(
        onClick = { localClipboardManager.setText(AnnotatedString(content)) }
      ) {
        Icon(
          imageVector = Icons.Outlined.ContentCopy,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(stringResource(id = android.R.string.copy))
      }
    },
  )
}

/**
 * Text display alert dialog
 */
@Composable
fun CustomTextAlertDialog(
  titleString: String,
  content: String,
  opened: MutableState<Boolean>,
) {
  CustomAlertDialog(
    titleString = titleString,
    titleIcon = Icons.AutoMirrored.Outlined.Help,
    opened = opened,
    content = {
      SelectionContainer {
        Text(
          text = content,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.verticalScroll(rememberScrollState())
        )
      }
    },
  )
}

/**
 * Base alert dialog - Material 3 styled
 */
@Composable
fun CustomAlertDialog(
  modifier: Modifier = Modifier,
  titleString: String,
  titleIcon: ImageVector,
  opened: MutableState<Boolean>,
  dismissButton: @Composable (() -> Unit)? = null,
  content: @Composable (() -> Unit)? = null,
) {
  AlertDialog(
    modifier = modifier.fillMaxWidth(),
    icon = {
      Icon(
        imageVector = titleIcon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
      )
    },
    title = {
      Text(
        text = titleString,
        style = MaterialTheme.typography.headlineSmall
      )
    },
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    onDismissRequest = { opened.value = false },
    text = content,
    confirmButton = {
      TextButton(onClick = { opened.value = false }) {
        Text(stringResource(id = android.R.string.ok))
      }
    },
    dismissButton = dismissButton,
  )
}

@Preview
@Composable
fun PassText() {
  Box {
    CustomCardBool("True", true)
  }
}

@Preview
@Composable
fun FailText() {
  Box {
    CustomCardBool("False", false)
  }
}
