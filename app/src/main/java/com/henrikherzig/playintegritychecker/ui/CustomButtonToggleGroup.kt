package com.henrikherzig.playintegritychecker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Material 3 Segmented Button Group
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleGroup(
  selectedIndex: String,
  items: List<List<String>>,
  indexChanged: (String) -> Unit,
  height: androidx.compose.ui.unit.Dp = 40.dp
) {
  SingleChoiceSegmentedButtonRow(
    modifier = Modifier.fillMaxWidth()
  ) {
    items.forEachIndexed { index, item ->
      SegmentedButton(
        selected = selectedIndex == item[0],
        onClick = { indexChanged(item[0]) },
        shape = SegmentedButtonDefaults.itemShape(
          index = index,
          count = items.size
        ),
        colors = SegmentedButtonDefaults.colors(
          activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
          activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          inactiveContainerColor = MaterialTheme.colorScheme.surface,
          inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
      ) {
        Text(
          text = item[1],
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}

/**
 * Multi-select toggle group (for future use)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiToggleGroup(
  selectedItems: Set<String>,
  items: List<List<String>>,
  onSelectionChange: (Set<String>) -> Unit
) {
  MultiChoiceSegmentedButtonRow(
    modifier = Modifier.fillMaxWidth()
  ) {
    items.forEachIndexed { index, item ->
      SegmentedButton(
        checked = selectedItems.contains(item[0]),
        onCheckedChange = { checked ->
          val newSelection = if (checked) {
            selectedItems + item[0]
          } else {
            selectedItems - item[0]
          }
          onSelectionChange(newSelection)
        },
        shape = SegmentedButtonDefaults.itemShape(
          index = index,
          count = items.size
        )
      ) {
        Text(
          text = item[1],
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}
