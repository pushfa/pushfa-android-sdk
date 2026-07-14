package com.pushfa.sdk.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pushfa.sdk.Pushfa
import com.pushfa.sdk.PushfaConfig
import com.pushfa.sdk.PushfaMessage
import java.net.HttpURLConnection
import java.net.URL

internal object NotificationRenderer {
    fun display(context: Context, config: PushfaConfig, message: PushfaMessage): Boolean {
        createChannel(context, config)
        if (!Pushfa.areNotificationsEnabled(context)) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(config.notificationChannelId)?.importance == NotificationManager.IMPORTANCE_NONE) {
                return false
            }
        }
        val appLabel = runCatching {
            context.applicationInfo.loadLabel(context.packageManager).toString()
        }.getOrDefault("Pushfa")
        // Android identifies an active notification by its (tag, id) pair. Both
        // values must therefore be stable for every message sharing collapse_id.
        val notificationKey = message.collapseId?.takeIf { it.isNotBlank() } ?: message.id
        val notificationId = notificationKey.hashCode() and Int.MAX_VALUE
        val smallIcon = config.smallIconResId.takeIf { it != 0 }
            ?: android.R.drawable.ic_dialog_info

        val contentIntent = PushfaNotificationClickActivity.pendingIntent(
            context = context,
            requestCode = notificationId,
            notificationId = message.id,
            clickAckUrl = message.clickAckUrl,
            targetUrl = message.url,
            actionId = "body",
        )
        val builder = NotificationCompat.Builder(context, config.notificationChannelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(message.title.ifBlank { appLabel })
            .setContentText(message.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(if (message.silent) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_HIGH)
            .setSilent(message.silent)
            .setOnlyAlertOnce(!message.collapseId.isNullOrBlank())
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        config.accentColor?.let(builder::setColor)

        val picture = downloadBitmap(message.imageUrl)
        if (picture != null) {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(picture)
                    .bigLargeIcon(null as Bitmap?),
            )
        } else {
            downloadBitmap(message.iconUrl)?.let(builder::setLargeIcon)
        }

        message.actions.take(2).forEachIndexed { index, action ->
            val actionIntent = PushfaNotificationClickActivity.pendingIntent(
                context = context,
                requestCode = notificationId + index + 1,
                notificationId = message.id,
                clickAckUrl = message.clickAckUrl,
                targetUrl = action.url?.takeIf { it.isNotBlank() } ?: message.url,
                actionId = action.id,
            )
            builder.addAction(0, action.title, actionIntent)
        }

        return try {
            NotificationManagerCompat.from(context).notify(
                notificationKey,
                notificationId,
                builder.build(),
            )
            PushfaReportWorker.enqueueDelivery(context, message.deliveryAckUrl, message.id)
            true
        } catch (_: SecurityException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }

    private fun createChannel(context: Context, config: PushfaConfig) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(config.notificationChannelId) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                config.notificationChannelId,
                config.notificationChannelName,
                config.notificationChannelImportance,
            ),
        )
    }

    private fun downloadBitmap(rawUrl: String?): Bitmap? {
        if (rawUrl.isNullOrBlank()) return null
        val url = runCatching { URL(rawUrl) }.getOrNull() ?: return null
        if (url.protocol != "https" && url.protocol != "http") return null
        val connection = runCatching { url.openConnection() as HttpURLConnection }.getOrNull() ?: return null
        return try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 7_000
            connection.instanceFollowRedirects = true
            connection.inputStream.use(BitmapFactory::decodeStream)
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
