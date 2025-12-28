package com.henrikherzig.playintegritychecker.background

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Helper object for scheduling and managing WorkManager tasks
 */
object WorkManagerScheduler {

  private const val TAG = "WorkManagerScheduler"
  private const val PERIODIC_WORK_NAME = "integrity_periodic_check"
  private const val IMMEDIATE_WORK_NAME = "integrity_immediate_check"

  /**
   * Available check intervals in minutes
   */
  enum class CheckInterval(val minutes: Int, val label: String) {
    FIFTEEN_MIN(15, "15m"),
    THIRTY_MIN(30, "30m"),
    ONE_HOUR(60, "1h"),
    SIX_HOURS(360, "6h")
  }

  /**
   * Schedule periodic integrity checks
   */
  fun schedulePeriodicCheck(context: Context, intervalMinutes: Int) {
    Log.d(TAG, "Scheduling periodic check every $intervalMinutes minutes")

    // WorkManager minimum is 15 minutes
    val actualInterval = maxOf(intervalMinutes, 15)

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val periodicWorkRequest = PeriodicWorkRequestBuilder<IntegrityCheckWorker>(
      actualInterval.toLong(),
      TimeUnit.MINUTES
    )
      .setConstraints(constraints)
      .addTag("integrity_check")
      .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      PERIODIC_WORK_NAME,
      ExistingPeriodicWorkPolicy.UPDATE,
      periodicWorkRequest
    )

    Log.d(TAG, "Periodic work scheduled successfully")
  }

  /**
   * Schedule an immediate one-time integrity check
   */
  fun scheduleImmediateCheck(context: Context) {
    Log.d(TAG, "Scheduling immediate integrity check")

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<IntegrityCheckWorker>()
      .setConstraints(constraints)
      .addTag("integrity_check_immediate")
      .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
      IMMEDIATE_WORK_NAME,
      ExistingWorkPolicy.REPLACE,
      oneTimeWorkRequest
    )

    Log.d(TAG, "Immediate work scheduled successfully")
  }

  /**
   * Cancel all periodic checks
   */
  fun cancelPeriodicCheck(context: Context) {
    Log.d(TAG, "Cancelling periodic checks")
    WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
  }

  /**
   * Cancel all integrity check work
   */
  fun cancelAllWork(context: Context) {
    Log.d(TAG, "Cancelling all integrity check work")
    WorkManager.getInstance(context).cancelAllWorkByTag("integrity_check")
    WorkManager.getInstance(context).cancelAllWorkByTag("integrity_check_immediate")
  }

  /**
   * Check if periodic work is currently scheduled
   */
  fun isPeriodicCheckScheduled(context: Context): Boolean {
    val workInfos = WorkManager.getInstance(context)
      .getWorkInfosForUniqueWork(PERIODIC_WORK_NAME)
      .get()

    return workInfos.any { !it.state.isFinished }
  }

  /**
   * Get interval label from minutes
   */
  fun getIntervalLabel(minutes: Int): String {
    return CheckInterval.entries.find { it.minutes == minutes }?.label
      ?: "$minutes minutes"
  }

  /**
   * Get all available intervals
   */
  fun getAvailableIntervals(): List<CheckInterval> {
    return CheckInterval.entries.toList()
  }
}
