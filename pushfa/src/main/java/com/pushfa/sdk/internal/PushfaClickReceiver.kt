package com.pushfa.sdk.internal

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri

internal class PushfaClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID).orEmpty()
        val clickAckUrl = intent.getStringExtra(EXTRA_CLICK_ACK_URL)
        val targetUrl = intent.getStringExtra(EXTRA_TARGET_URL)

        PushfaReportWorker.enqueueClick(context, clickAckUrl, notificationId)
        if (!targetUrl.isNullOrBlank() && !DO_NOT_OPEN.containsMatchIn(targetUrl)) {
            val openIntent = runCatching {
                Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.getOrNull()
            if (openIntent != null) {
                runCatching { context.startActivity(openIntent) }
            }
        } else if (targetUrl.isNullOrBlank()) {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                runCatching { context.startActivity(it) }
            }
        }
    }

    companion object {
        private const val EXTRA_NOTIFICATION_ID = "pushfa_notification_id"
        private const val EXTRA_CLICK_ACK_URL = "pushfa_click_ack_url"
        private const val EXTRA_TARGET_URL = "pushfa_target_url"
        private const val EXTRA_ACTION_ID = "pushfa_action_id"
        private val DO_NOT_OPEN = Regex("[?&]_osp=do_not_open(?:&|=|$)", RegexOption.IGNORE_CASE)

        fun pendingIntent(
            context: Context,
            requestCode: Int,
            notificationId: String,
            clickAckUrl: String?,
            targetUrl: String?,
            actionId: String,
        ): PendingIntent {
            val intent = Intent(context, PushfaClickReceiver::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(EXTRA_CLICK_ACK_URL, clickAckUrl)
                putExtra(EXTRA_TARGET_URL, targetUrl)
                putExtra(EXTRA_ACTION_ID, actionId)
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
