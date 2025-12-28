package com.henrikherzig.playintegritychecker.background

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.gson.Gson
import com.henrikherzig.playintegritychecker.BuildConfig
import com.henrikherzig.playintegritychecker.attestation.PlayIntegrityStatement
import com.henrikherzig.playintegritychecker.notifications.IntegrityNotificationManager
import androidx.glance.appwidget.updateAll
import com.henrikherzig.playintegritychecker.widget.IntegrityGlanceWidget
import com.henrikherzig.playintegritychecker.widget.IntegrityGlanceWidgetMedium
import com.henrikherzig.playintegritychecker.widget.WidgetStateManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwx.JsonWebStructure
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.floor

/**
 * Background worker that performs periodic Play Integrity checks
 */
class IntegrityCheckWorker(
  private val context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params) {

  companion object {
    private const val TAG = "IntegrityCheckWorker"
  }

  override suspend fun doWork(): Result {
    Log.d(TAG, "Starting integrity check...")

    return try {
      // Get previous state to compare
      val previousState = WidgetStateManager.getIntegrityState(context)
      val alertsEnabled = WidgetStateManager.getAlertsEnabled(context).first()

      // Generate nonce
      val nonce = generateNonce(50)

      // Request integrity token
      val integrityToken = requestIntegrityToken(nonce)

      // Decode locally
      val statement = decodeLocally(integrityToken)

      // Extract verdicts
      val deviceVerdict = statement.deviceIntegrity?.deviceRecognitionVerdict ?: arrayListOf()
      val appVerdict = statement.appIntegrity?.appRecognitionVerdict
      val licensingVerdict = statement.accountDetails?.appLicensingVerdict

      // Save result
      WidgetStateManager.saveIntegrityResult(
        context,
        deviceVerdict,
        appVerdict,
        licensingVerdict
      )

      // Check for verdict changes and send notification if needed
      if (alertsEnabled && previousState.lastCheckTimestamp > 0) {
        checkForChangesAndNotify(previousState, deviceVerdict, appVerdict, licensingVerdict)
      }

      // Update widgets
      updateWidgets()

      Log.d(TAG, "Integrity check completed successfully")
      Result.success()

    } catch (e: Exception) {
      Log.e(TAG, "Integrity check failed", e)
      WidgetStateManager.saveIntegrityError(context, e.message ?: "Unknown error")
      updateWidgets()
      Result.retry()
    }
  }

  /**
   * Request integrity token from Play Integrity API
   */
  private suspend fun requestIntegrityToken(nonce: String): String {
    return suspendCancellableCoroutine { continuation ->
      val integrityManager = IntegrityManagerFactory.create(context)

      val request = IntegrityTokenRequest.builder()
        .setNonce(nonce)
        .build()

      integrityManager.requestIntegrityToken(request)
        .addOnSuccessListener { response ->
          continuation.resume(response.token())
        }
        .addOnFailureListener { e ->
          continuation.resumeWithException(e)
        }
    }
  }

  /**
   * Decode integrity token locally using keys from BuildConfig
   */
  private fun decodeLocally(integrityToken: String): PlayIntegrityStatement {
    val base64OfEncodedDecryptionKey = BuildConfig.base64_of_encoded_decryption_key
    val base64OfEncodedVerificationKey = BuildConfig.base64_of_encoded_verification_key

    if (base64OfEncodedDecryptionKey.isNullOrEmpty() || base64OfEncodedVerificationKey.isNullOrEmpty()) {
      throw IllegalStateException("BuildConfig keys not configured. Check local.properties.")
    }

    val decryptionKeyBytes: ByteArray =
      Base64.decode(base64OfEncodedDecryptionKey, Base64.DEFAULT)

    val decryptionKey: SecretKey = SecretKeySpec(
      decryptionKeyBytes,
      0,
      decryptionKeyBytes.size,
      "AES"
    )

    val encodedVerificationKey: ByteArray =
      Base64.decode(base64OfEncodedVerificationKey, Base64.DEFAULT)

    val verificationKey: PublicKey = KeyFactory.getInstance("EC")
      .generatePublic(X509EncodedKeySpec(encodedVerificationKey))

    val jwe: JsonWebEncryption =
      JsonWebStructure.fromCompactSerialization(integrityToken) as JsonWebEncryption
    jwe.key = decryptionKey

    val compactJws = jwe.payload

    val jws: JsonWebSignature =
      JsonWebStructure.fromCompactSerialization(compactJws) as JsonWebSignature
    jws.key = verificationKey

    val payload: String = jws.payload
    return Gson().fromJson(payload, PlayIntegrityStatement::class.java)
  }

  /**
   * Generate a local nonce
   */
  private fun generateNonce(length: Int): String {
    var nonce = ""
    val allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    for (i in 0 until length) {
      nonce += allowed[floor(Math.random() * allowed.length).toInt()].toString()
    }
    return Base64.encodeToString(nonce.toByteArray(Charsets.UTF_8), Base64.URL_SAFE)
  }

  /**
   * Check if verdicts changed and send notification
   */
  private fun checkForChangesAndNotify(
    previousState: WidgetStateManager.IntegrityState,
    newDeviceVerdict: List<String>,
    newAppVerdict: String?,
    newLicensingVerdict: String?
  ) {
    val previousDeviceSet = previousState.deviceVerdict.toSet()
    val newDeviceSet = newDeviceVerdict.toSet()

    val deviceChanged = previousDeviceSet != newDeviceSet
    val appChanged = previousState.appVerdict != newAppVerdict
    val licensingChanged = previousState.licensingVerdict != newLicensingVerdict

    if (deviceChanged || appChanged || licensingChanged) {
      val changes = buildString {
        if (deviceChanged) {
          append("Device: ${formatVerdictChange(previousState.deviceVerdict, newDeviceVerdict)}")
        }
        if (appChanged) {
          if (isNotEmpty()) append(", ")
          append("App: ${previousState.appVerdict ?: "N/A"} -> ${newAppVerdict ?: "N/A"}")
        }
        if (licensingChanged) {
          if (isNotEmpty()) append(", ")
          append("License: ${previousState.licensingVerdict ?: "N/A"} -> ${newLicensingVerdict ?: "N/A"}")
        }
      }

      IntegrityNotificationManager.showVerdictChangeNotification(
        context,
        title = "Integrity Status Changed",
        message = changes,
        isImprovement = isImprovement(previousState.overallStatus, newDeviceVerdict)
      )
    }
  }

  private fun formatVerdictChange(old: List<String>, new: List<String>): String {
    val oldLabel = getHighestVerdict(old)
    val newLabel = getHighestVerdict(new)
    return "$oldLabel -> $newLabel"
  }

  private fun getHighestVerdict(verdicts: List<String>): String {
    return when {
      verdicts.contains("MEETS_STRONG_INTEGRITY") -> "STRONG"
      verdicts.contains("MEETS_DEVICE_INTEGRITY") -> "DEVICE"
      verdicts.contains("MEETS_BASIC_INTEGRITY") -> "BASIC"
      else -> "NONE"
    }
  }

  private fun isImprovement(
    oldStatus: WidgetStateManager.OverallStatus,
    newDeviceVerdict: List<String>
  ): Boolean {
    val newHasStrong = newDeviceVerdict.contains("MEETS_STRONG_INTEGRITY")
    val newHasDevice = newDeviceVerdict.contains("MEETS_DEVICE_INTEGRITY")
    val newHasBasic = newDeviceVerdict.contains("MEETS_BASIC_INTEGRITY")

    return when (oldStatus) {
      WidgetStateManager.OverallStatus.FAILED -> newHasBasic || newHasDevice || newHasStrong
      WidgetStateManager.OverallStatus.BASIC -> newHasDevice || newHasStrong
      WidgetStateManager.OverallStatus.DEVICE -> newHasStrong
      else -> false
    }
  }

  /**
   * Update all widgets after check completes
   */
  private suspend fun updateWidgets() {
    try {
      IntegrityGlanceWidget().updateAll(context)
      IntegrityGlanceWidgetMedium().updateAll(context)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to update widgets", e)
    }
  }
}
