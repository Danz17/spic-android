package com.henrikherzig.playintegritychecker.ui

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.ui.theme.IntegrityColors

/**
 * Opens a link in a preview window within the app
 */
fun openLink(url: String, context: Context) {
  CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
}

/**
 * Row of link buttons
 */
@Composable
fun CustomButtonRow(context: Context, linkIdPairs: List<Pair<String, String>>) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    linkIdPairs.forEachIndexed { _, item ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        CustomButton(
          onClick = { openLink(item.first, context) },
          icon = Icons.AutoMirrored.Outlined.OpenInNew,
          text = item.second
        )
      }
    }
  }
}

/**
 * Help button
 */
@Composable
fun CustomHelpButton(onClick: () -> Unit) {
  IconButton(
    onClick = onClick,
    modifier = Modifier.size(24.dp)
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Outlined.Help,
      contentDescription = "Help",
      modifier = Modifier.size(20.dp),
      tint = MaterialTheme.colorScheme.primary
    )
  }
}

/**
 * Close button
 */
@Composable
fun CustomCloseButton(onClick: () -> Unit) {
  IconButton(
    onClick = onClick,
    modifier = Modifier.size(24.dp)
  ) {
    Icon(
      imageVector = Icons.Outlined.Close,
      contentDescription = "Close",
      modifier = Modifier.size(20.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

/**
 * Three-dot integrity indicator
 * state: 0=none, 1=basic, 2=device, 3=strong
 */
@Composable
fun CustomThreeStateIcons(state: Int, text: String) {
  val color = if (text == "MEETS_VIRTUAL_INTEGRITY") IntegrityColors.virtual else when (state) {
    0 -> IntegrityColors.none
    1 -> IntegrityColors.basic
    2 -> IntegrityColors.device
    3 -> IntegrityColors.strong
    else -> MaterialTheme.colorScheme.outline
  }

  Row(verticalAlignment = Alignment.CenterVertically) {
    // Three dot indicators
    for (i in 1..3) {
      Icon(
        imageVector = if (state >= i) Icons.Filled.Circle else Icons.Outlined.Circle,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = color
      )
      if (i < 3) Spacer(modifier = Modifier.width(4.dp))
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.titleMedium,
      color = color
    )
  }
}

data class HorizontalPagerContent(
  val threeState: Pair<Int, String>,
  val text: String,
)

data class HorizontalPagerContentText(
  val title: String,
  val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSlideStackDeviceRecognitionVerdict(initialPage: Int) {
  @Composable
  fun createItems() = listOf(
    HorizontalPagerContent(
      threeState = Pair(0, "NO_INTEGRITY"),
      text = stringResource(id = R.string.deviceRecognition_help_NO_INTEGRITY)
    ),
    HorizontalPagerContent(
      threeState = Pair(1, "MEETS_BASIC_INTEGRITY"),
      text = stringResource(id = R.string.deviceRecognition_help_MEETS_BASIC_INTEGRITY)
    ),
    HorizontalPagerContent(
      threeState = Pair(2, "MEETS_DEVICE_INTEGRITY"),
      text = stringResource(id = R.string.deviceRecognition_help_MEETS_DEVICE_INTEGRITY)
    ),
    HorizontalPagerContent(
      threeState = Pair(3, "MEETS_STRONG_INTEGRITY"),
      text = stringResource(id = R.string.deviceRecognition_help_MEETS_STRONG_INTEGRITY)
    ),
    HorizontalPagerContent(
      threeState = Pair(0, "MEETS_VIRTUAL_INTEGRITY"),
      text = stringResource(id = R.string.deviceRecognition_help_MEETS_VIRTUAL_INTEGRITY)
    )
  )

  val items = createItems()
  val pagerState = rememberPagerState(initialPage = initialPage) { items.size }

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    HorizontalPager(state = pagerState) { page ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxHeight()
          .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
      ) {
        val item = items[page]
        CustomThreeStateIcons(item.threeState.first, item.threeState.second)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = item.text,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    PageIndicator(
      numberOfPages = items.size,
      selectedPage = pagerState.currentPage,
      selectedColor = MaterialTheme.colorScheme.primary,
      defaultColor = MaterialTheme.colorScheme.outlineVariant,
      defaultRadius = 8.dp,
      selectedLength = 24.dp,
      space = 8.dp,
      animationDurationInMillis = 300,
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSlideStackRecentDeviceActivity(initialPage: Int) {
  @Composable
  fun createItems() = listOf(
    HorizontalPagerContentText(
      title = "LEVEL_1",
      description = stringResource(id = R.string.recentDeviceActivity_help_LEVEL_1)
    ),
    HorizontalPagerContentText(
      title = "LEVEL_2",
      description = stringResource(id = R.string.recentDeviceActivity_help_LEVEL_2)
    ),
    HorizontalPagerContentText(
      title = "LEVEL_3",
      description = stringResource(id = R.string.recentDeviceActivity_help_LEVEL_3)
    ),
    HorizontalPagerContentText(
      title = "LEVEL_4",
      description = stringResource(id = R.string.recentDeviceActivity_help_LEVEL_4)
    ),
    HorizontalPagerContentText(
      title = "UNEVALUATED",
      description = stringResource(id = R.string.recentDeviceActivity_help_UNEVALUATED)
    ),
  )

  val items = createItems()
  val pagerState = rememberPagerState(initialPage = initialPage) { items.size }

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    HorizontalPager(state = pagerState) { page ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxHeight()
          .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
      ) {
        val item = items[page]
        CustomCardTitle2(item.title)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = item.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    PageIndicator(
      numberOfPages = items.size,
      selectedPage = pagerState.currentPage,
      selectedColor = MaterialTheme.colorScheme.primary,
      defaultColor = MaterialTheme.colorScheme.outlineVariant,
      defaultRadius = 8.dp,
      selectedLength = 24.dp,
      space = 8.dp,
      animationDurationInMillis = 300,
    )
  }
}

@Composable
fun PageIndicator(
  numberOfPages: Int,
  modifier: Modifier = Modifier,
  selectedPage: Int = 0,
  selectedColor: Color = MaterialTheme.colorScheme.primary,
  defaultColor: Color = MaterialTheme.colorScheme.outlineVariant,
  defaultRadius: Dp = 8.dp,
  selectedLength: Dp = 24.dp,
  space: Dp = 8.dp,
  animationDurationInMillis: Int = 300,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(space),
    modifier = modifier,
  ) {
    for (i in 0 until numberOfPages) {
      val isSelected = i == selectedPage
      PageIndicatorView(
        isSelected = isSelected,
        selectedColor = selectedColor,
        defaultColor = defaultColor,
        defaultRadius = defaultRadius,
        selectedLength = selectedLength,
        animationDurationInMillis = animationDurationInMillis,
      )
    }
  }
}

@Composable
fun PageIndicatorView(
  isSelected: Boolean,
  selectedColor: Color,
  defaultColor: Color,
  defaultRadius: Dp,
  selectedLength: Dp,
  animationDurationInMillis: Int,
  modifier: Modifier = Modifier,
) {
  val color: Color by animateColorAsState(
    targetValue = if (isSelected) selectedColor else defaultColor,
    animationSpec = tween(durationMillis = animationDurationInMillis),
    label = "indicator_color"
  )
  val width: Dp by animateDpAsState(
    targetValue = if (isSelected) selectedLength else defaultRadius,
    animationSpec = tween(durationMillis = animationDurationInMillis),
    label = "indicator_width"
  )

  Canvas(
    modifier = modifier.size(width = width, height = defaultRadius)
  ) {
    drawRoundRect(
      color = color,
      topLeft = Offset.Zero,
      size = Size(width = width.toPx(), height = defaultRadius.toPx()),
      cornerRadius = CornerRadius(x = defaultRadius.toPx(), y = defaultRadius.toPx())
    )
  }
}
