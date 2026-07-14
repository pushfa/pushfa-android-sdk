package com.pushfa.sdk.internal

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.pushfa.sdk.Pushfa

/**
 * Activity-backed notification target required by Android 12+.
 *
 * A BroadcastReceiver or Service cannot launch the final destination after a
 * notification tap on Android 12+, because that is a notification trampoline.
 * This transparent Activity records the click and routes to the external URI or
 * host application's launcher without displaying its own UI.
 */
internal class PushfaNotificationClickActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
        finish()
    }

    private fun handleNotificationIntent(intent: Intent) {
        val notificationId = intent.getStringExtra(Pushfa.EXTRA_NOTIFICATION_ID).orEmpty()
        val clickAckUrl = intent.getStringExtra(EXTRA_CLICK_ACK_URL)
        val targetUrl = intent.getStringExtra(Pushfa.EXTRA_TARGET_URL)
        val actionId = intent.getStringExtra(Pushfa.EXTRA_ACTION_ID).orEmpty()

        PushfaReportWorker.enqueueClick(applicationContext, clickAckUrl, notificationId)
        if (!targetUrl.isNullOrBlank() && DO_NOT_OPEN.containsMatchIn(targetUrl)) return

        if (targetUrl.isNullOrBlank() || isAppRelative(targetUrl)) {
            openHostApp(targetUrl, notificationId, actionId)
            return
        }

        val normalizedUrl = when {
            targetUrl.startsWith("//") -> "https:$targetUrl"
            targetUrl.startsWith("www.", ignoreCase = true) -> "https://$targetUrl"
            else -> targetUrl
        }
        val opened = try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl)))
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }

        if (!opened) openHostApp(targetUrl, notificationId, actionId)
    }

    /**
     * Relative targets such as /promotion belong to the host app. The launcher
     * receives the original path as a public Pushfa extra so any navigation stack
     * can map it to its own screen without an SDK dependency on that framework.
     */
    private fun openHostApp(targetUrl: String?, notificationId: String, actionId: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        launchIntent
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(Pushfa.EXTRA_TARGET_URL, targetUrl)
            .putExtra(Pushfa.EXTRA_NOTIFICATION_ID, notificationId)
            .putExtra(Pushfa.EXTRA_ACTION_ID, actionId)
        if (!targetUrl.isNullOrBlank()) launchIntent.data = Uri.parse(targetUrl)
        runCatching { startActivity(launchIntent) }
    }

    private fun isAppRelative(targetUrl: String): Boolean {
        if (targetUrl.startsWith("//") || targetUrl.startsWith("www.", ignoreCase = true)) return false
        return Uri.parse(targetUrl).scheme.isNullOrBlank()
    }

    companion object {
        private const val EXTRA_CLICK_ACK_URL = "com.pushfa.sdk.extra.CLICK_ACK_URL"
        private val DO_NOT_OPEN = Regex("[?&]_osp=do_not_open(?:&|=|$)", RegexOption.IGNORE_CASE)

        fun pendingIntent(
            context: Context,
            requestCode: Int,
            notificationId: String,
            clickAckUrl: String?,
            targetUrl: String?,
            actionId: String,
        ): PendingIntent {
            val intent = Intent(context, PushfaNotificationClickActivity::class.java).apply {
                putExtra(Pushfa.EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(EXTRA_CLICK_ACK_URL, clickAckUrl)
                putExtra(Pushfa.EXTRA_TARGET_URL, targetUrl)
                putExtra(Pushfa.EXTRA_ACTION_ID, actionId)
            }
            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
