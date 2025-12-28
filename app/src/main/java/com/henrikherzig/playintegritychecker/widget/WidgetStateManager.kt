package com.henrikherzig.playintegritychecker.widget

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.henrikherzig.playintegritychecker.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manages widget state persistence using DataStore
 */
object WidgetStateManager {
  // DataStore keys for widget state
  private val LAST_DEVICE_VERDICT = stringPreferencesKey("last_device_verdict")
  private val LAST_APP_VERDICT = stringPreferencesKey("last_app_verdict")
  private val LAST_LICENSING_VERDICT = stringPreferencesKey("last_licensing_verdict")
  private val LAST_CHECK_TIMESTAMP = longPreferencesKey("last_check_timestamp")
  private val LAST_CHECK_SUCCESS = booleanPreferencesKey("last_check_success")
  private val LAST_ERROR_MESSAGE = stringPreferencesKey("last_error_message")

  // Settings keys
  val WIDGET_REFRESH_ENABLED = booleanPreferencesKey("widget_refresh_enabled")
  val CHECK_INTERVAL_MINUTES = intPreferencesKey("check_interval_minutes")
  val ALERTS_ENABLED = booleanPreferencesKey("alerts_enabled")

  /**
   * Data class representing widget state
   */
  data class IntegrityState(
    val deviceVerdict: List<String> = emptyList(),
    val appVerdict: String? = null,
    val licensingVerdict: String? = null,
    val lastCheckTimestamp: Long = 0L,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
  ) {
    val hasStrongIntegrity: Boolean
      get() = deviceVerdict.contains("MEETS_STRONG_INTEGRITY")

    val hasDeviceIntegrity: Boolean
      get() = deviceVerdict.contains("MEETS_DEVICE_INTEGRITY")

    val hasBasicIntegrity: Boolean
      get() = deviceVerdict.contains("MEETS_BASIC_INTEGRITY")

    val overallStatus: OverallStatus
      get() = when {
        lastCheckTimestamp == 0L -> OverallStatus.UNKNOWN
        !isSuccess && !errorMessage.isNullOrEmpty() -> OverallStatus.ERROR
        hasStrongIntegrity -> OverallStatus.STRONG
        hasDeviceIntegrity -> OverallStatus.DEVICE
        hasBasicIntegrity -> OverallStatus.BASIC
        else -> OverallStatus.FAILED
      }
  }

  enum class OverallStatus {
    STRONG,   // All integrity checks passed
    DEVICE,   // Device integrity passed but not strong
    BASIC,    // Only basic integrity
    FAILED,   // No integrity verdicts
    ERROR,    // Check failed with error
    UNKNOWN   // Never checked
  }

  /**
   * Get current integrity state as Flow
   */
  fun getIntegrityStateFlow(context: Context): Flow<IntegrityState> {
    return context.dataStore.data.map { preferences ->
      IntegrityState(
        deviceVerdict = preferences[LAST_DEVICE_VERDICT]?.split(",")?.filter { it.isNotEmpty() }
          ?: emptyList(),
        appVerdict = preferences[LAST_APP_VERDICT],
        licensingVerdict = preferences[LAST_LICENSING_VERDICT],
        lastCheckTimestamp = preferences[LAST_CHECK_TIMESTAMP] ?: 0L,
        isSuccess = preferences[LAST_CHECK_SUCCESS] ?: false,
        errorMessage = preferences[LAST_ERROR_MESSAGE]
      )
    }
  }

  /**
   * Get current integrity state (suspend)
   */
  suspend fun getIntegrityState(context: Context): IntegrityState {
    return getIntegrityStateFlow(context).first()
  }

  /**
   * Save successful integrity check result
   */
  suspend fun saveIntegrityResult(
    context: Context,
    deviceVerdict: List<String>,
    appVerdict: String?,
    licensingVerdict: String?
  ) {
    context.dataStore.edit { preferences ->
      preferences[LAST_DEVICE_VERDICT] = deviceVerdict.joinToString(",")
      preferences[LAST_APP_VERDICT] = appVerdict ?: ""
      preferences[LAST_LICENSING_VERDICT] = licensingVerdict ?: ""
      preferences[LAST_CHECK_TIMESTAMP] = System.currentTimeMillis()
      preferences[LAST_CHECK_SUCCESS] = true
      preferences[LAST_ERROR_MESSAGE] = ""
    }
  }

  /**
   * Save failed integrity check result
   */
  suspend fun saveIntegrityError(context: Context, errorMessage: String) {
    context.dataStore.edit { preferences ->
      preferences[LAST_CHECK_TIMESTAMP] = System.currentTimeMillis()
      preferences[LAST_CHECK_SUCCESS] = false
      preferences[LAST_ERROR_MESSAGE] = errorMessage
    }
  }

  /**
   * Get widget refresh enabled setting
   */
  fun getWidgetRefreshEnabled(context: Context): Flow<Boolean> {
    return context.dataStore.data.map { preferences ->
      preferences[WIDGET_REFRESH_ENABLED] ?: true
    }
  }

  /**
   * Get check interval in minutes
   */
  fun getCheckIntervalMinutes(context: Context): Flow<Int> {
    return context.dataStore.data.map { preferences ->
      preferences[CHECK_INTERVAL_MINUTES] ?: 60 // Default 1 hour
    }
  }

  /**
   * Get alerts enabled setting
   */
  fun getAlertsEnabled(context: Context): Flow<Boolean> {
    return context.dataStore.data.map { preferences ->
      preferences[ALERTS_ENABLED] ?: true
    }
  }

  /**
   * Save widget settings
   */
  suspend fun saveWidgetSettings(
    context: Context,
    refreshEnabled: Boolean,
    intervalMinutes: Int,
    alertsEnabled: Boolean
  ) {
    context.dataStore.edit { preferences ->
      preferences[WIDGET_REFRESH_ENABLED] = refreshEnabled
      preferences[CHECK_INTERVAL_MINUTES] = intervalMinutes
      preferences[ALERTS_ENABLED] = alertsEnabled
    }
  }

  /**
   * Format timestamp to relative time string
   */
  fun formatRelativeTime(timestamp: Long): String {
    if (timestamp == 0L) return "Never"

    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
      diff < 60_000 -> "Just now"
      diff < 3600_000 -> "${diff / 60_000} min ago"
      diff < 86400_000 -> "${diff / 3600_000} hr ago"
      else -> "${diff / 86400_000} days ago"
    }
  }
}
