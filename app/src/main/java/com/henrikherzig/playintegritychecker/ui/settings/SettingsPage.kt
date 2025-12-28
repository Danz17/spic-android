package com.henrikherzig.playintegritychecker.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henrikherzig.playintegritychecker.R
import com.henrikherzig.playintegritychecker.background.WorkManagerScheduler
import com.henrikherzig.playintegritychecker.dataStore
import com.henrikherzig.playintegritychecker.ui.*

@Composable
fun Settings(playServiceVersion: String?) {
    // get context, viewModel and stateTheme observer for detecting/setting ui changes
    val context = LocalContext.current
    val viewModel = remember { CustomViewModel(context.dataStore) }
    val themeValue = viewModel.stateTheme.observeAsState().value

    // variable to store state of theme selector
    val themeSelectorMode by remember(themeValue) {
        val mode = when (themeValue) {
            null -> "system"
            true -> "dark"
            false -> "light"
        }
        mutableStateOf(mode)
    }
    // theme selector options
    val system: String = stringResource(id = R.string.settings_theme_system)
    val light: String = stringResource(id = R.string.settings_theme_light)
    val dark: String = stringResource(id = R.string.settings_theme_dark)

    val themeSelectorOptions: List<List<String>> =
        listOf(listOf("system", system), listOf("light", light), listOf("dark", dark))
    val modeChanged: (String) -> Unit = {
        when (it) {
            "system" -> viewModel.switchToUseSystemSettings(true)
            "dark" -> viewModel.switchToUseDarkMode(true)
            "light" -> viewModel.switchToUseDarkMode(false)
        }
    }

    // update viewModel variables on change
    LaunchedEffect(viewModel) {
        viewModel.requestTheme()
        viewModel.requestURL()
        viewModel.requestWidgetSettings()
    }

    // Widget settings states
    val widgetRefreshEnabled = viewModel.stateWidgetRefreshEnabled.observeAsState(true).value
    val checkIntervalMinutes = viewModel.stateCheckIntervalMinutes.observeAsState(60).value
    val alertsEnabled = viewModel.stateAlertsEnabled.observeAsState(true).value

    // Interval options
    val intervalOptions = WorkManagerScheduler.getAvailableIntervals()

    // get url variable for textField
    val urlValue = viewModel.stateURL.observeAsState().value

    // variable for textField to set serverURL
    var text by remember(urlValue) {
        val text = when (urlValue) {
            null -> ""
            else -> urlValue
        }
        mutableStateOf(text)
    }

    // if clicked outside of the TextInputField for entering ServerURL the focus to it should be lost
    val interactionSource = MutableInteractionSource()
    var hideKeyboard by remember { mutableStateOf(false) }

    // define settings UI
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { hideKeyboard = true }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Device Info Section
            DeviceInfoContent(playServiceVersion)

            Spacer(Modifier.height(20.dp))

            // App Settings Section
            CustomElevatedCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(16.dp))

                    // Theme selector
                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    ToggleGroup(themeSelectorMode, themeSelectorOptions, modeChanged, 40.dp)

                    Spacer(Modifier.height(20.dp))

                    // Server URL
                    Text(
                        text = stringResource(R.string.settings_url),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = text,
                        onValueChange = { text = it },
                        hideKeyboard = hideKeyboard,
                        onFocusClear = {
                            viewModel.setURL(text)
                            hideKeyboard = false
                        },
                        onSearch = { viewModel.setURL(text) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        placeholder = {
                            Text(
                                stringResource(R.string.settings_url_hint),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Widget & Monitoring Section
            CustomElevatedCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_widget_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(16.dp))

                    // Background checks toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_widget_refresh),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = widgetRefreshEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.setWidgetRefreshEnabled(enabled)
                                if (enabled) {
                                    WorkManagerScheduler.schedulePeriodicCheck(context, checkIntervalMinutes)
                                } else {
                                    WorkManagerScheduler.cancelPeriodicCheck(context)
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Check interval
                    Text(
                        text = stringResource(R.string.settings_check_interval),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    val intervalSelectorOptions: List<List<String>> = intervalOptions.map {
                        listOf(it.minutes.toString(), it.label)
                    }
                    val currentIntervalMode = checkIntervalMinutes.toString()
                    ToggleGroup(
                        currentIntervalMode,
                        intervalSelectorOptions,
                        indexChanged = { newMinutes ->
                            val minutes = newMinutes.toIntOrNull() ?: 60
                            viewModel.setCheckIntervalMinutes(minutes)
                            if (widgetRefreshEnabled) {
                                WorkManagerScheduler.schedulePeriodicCheck(context, minutes)
                            }
                        },
                        height = 40.dp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Alerts toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_alerts_enabled),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = alertsEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.setAlertsEnabled(enabled)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}