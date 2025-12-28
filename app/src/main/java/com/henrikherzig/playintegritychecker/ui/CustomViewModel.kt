package com.henrikherzig.playintegritychecker.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CustomViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val forceDarkModeKey = booleanPreferencesKey("theme")
    private val serverURL = stringPreferencesKey("server_url")

    // Widget and background check settings keys
    private val widgetRefreshEnabledKey = booleanPreferencesKey("widget_refresh_enabled")
    private val checkIntervalMinutesKey = intPreferencesKey("check_interval_minutes")
    private val alertsEnabledKey = booleanPreferencesKey("alerts_enabled")

    val stateTheme = MutableLiveData<Boolean?>(null)
    val stateURL: MutableLiveData<String> = MutableLiveData("")

    // Widget and background check states
    val stateWidgetRefreshEnabled = MutableLiveData(true)
    val stateCheckIntervalMinutes = MutableLiveData(60)
    val stateAlertsEnabled = MutableLiveData(true)

    fun requestTheme() {
        viewModelScope.launch {
            dataStore.data.collectLatest {
                stateTheme.value = it[forceDarkModeKey]
            }
        }
    }

    fun requestURL() {
        viewModelScope.launch {
            dataStore.data.collectLatest {
                stateURL.value = it[serverURL]
            }
        }
    }

    fun switchToUseSystemSettings(isSystemSettings: Boolean) {
        viewModelScope.launch {
            if (isSystemSettings) {
                dataStore.edit {
                    it.remove(forceDarkModeKey)
                }
            }
        }
    }

    fun switchToUseDarkMode(isDarkTheme: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[forceDarkModeKey] = isDarkTheme
            }
        }
    }

    fun setURL(url: String) {
        if (url == "") {
            removeURL()
            return
        }
        viewModelScope.launch {
            dataStore.edit {
                it[serverURL] = url
            }
        }
    }

    private fun removeURL() {
        viewModelScope.launch {
            dataStore.edit {
                it.remove(serverURL)
            }
        }
    }

    // Widget and background check settings functions
    fun requestWidgetSettings() {
        viewModelScope.launch {
            dataStore.data.collectLatest {
                stateWidgetRefreshEnabled.value = it[widgetRefreshEnabledKey] ?: true
                stateCheckIntervalMinutes.value = it[checkIntervalMinutesKey] ?: 60
                stateAlertsEnabled.value = it[alertsEnabledKey] ?: true
            }
        }
    }

    fun setWidgetRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[widgetRefreshEnabledKey] = enabled
            }
        }
    }

    fun setCheckIntervalMinutes(minutes: Int) {
        viewModelScope.launch {
            dataStore.edit {
                it[checkIntervalMinutesKey] = minutes
            }
        }
    }

    fun setAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[alertsEnabledKey] = enabled
            }
        }
    }
}