package com.pushfa.sdk.internal

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

internal class PushfaReportWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        val url = inputData.getString(KEY_URL) ?: return Result.success()
        val notificationId = inputData.getString(KEY_NOTIFICATION_ID) ?: return Result.success()
        val type = inputData.getString(KEY_TYPE) ?: return Result.success()
        val createdAt = inputData.getLong(KEY_CREATED_AT, 0L)
        if (createdAt > 0 && System.currentTimeMillis() - createdAt > MAX_AGE_MS) return Result.success()

        val payload = if (type == TYPE_CLICK) {
            JSONObject().put("id", notificationId)
        } else {
            JSONObject().put(
                "notification",
                JSONObject().put("data", JSONObject().put("id", notificationId)),
            )
        }

        return if (post(url, payload.toString())) Result.success() else Result.retry()
    }

    private fun post(rawUrl: String, body: String): Boolean {
        val connection = try {
            URL(rawUrl).openConnection() as HttpURLConnection
        } catch (_: Exception) {
            return true
        }
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15_000
            connection.readTimeout = 20_000
            connection.doOutput = true
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.setRequestProperty("User-Agent", "Pushfa-Android/${PushfaSdkInfo.VERSION}")
            connection.outputStream.use { it.write(body.toByteArray(StandardCharsets.UTF_8)) }
            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        private const val KEY_URL = "url"
        private const val KEY_NOTIFICATION_ID = "notification_id"
        private const val KEY_TYPE = "type"
        private const val KEY_CREATED_AT = "created_at"
        private const val TYPE_DELIVERY = "delivery"
        private const val TYPE_CLICK = "click"
        private const val MAX_AGE_MS = 24 * 60 * 60 * 1000L

        fun enqueueDelivery(context: Context, url: String?, notificationId: String) {
            enqueue(context, url, notificationId, TYPE_DELIVERY)
        }

        fun enqueueClick(context: Context, url: String?, notificationId: String) {
            enqueue(context, url, notificationId, TYPE_CLICK)
        }

        private fun enqueue(context: Context, url: String?, notificationId: String, type: String) {
            if (url.isNullOrBlank() || notificationId.isBlank()) return
            val data = Data.Builder()
                .putString(KEY_URL, url)
                .putString(KEY_NOTIFICATION_ID, notificationId)
                .putString(KEY_TYPE, type)
                .putLong(KEY_CREATED_AT, System.currentTimeMillis())
                .build()
            val request = OneTimeWorkRequestBuilder<PushfaReportWorker>()
                .setInputData(data)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "pushfa-report-$type-$notificationId",
                ExistingWorkPolicy.KEEP,
                request,
            )
        }
    }
}
