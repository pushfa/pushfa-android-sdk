package com.pushfa.sdk

import android.app.NotificationManager

data class PushfaConfig @JvmOverloads constructor(
    val apiPublicKey: String,
    val baseUrl: String = "https://pushfa.com",
    val autoRegister: Boolean = true,
    val autoDisplayNotifications: Boolean = true,
    val trackVisits: Boolean = true,
    val notificationChannelId: String = "pushfa_default",
    val notificationChannelName: String = "Pushfa notifications",
    val notificationChannelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
    val smallIconResId: Int = 0,
    val accentColor: Int? = null,
) {
    init {
        require(apiPublicKey.isNotBlank()) { "apiPublicKey must not be blank." }
        require(baseUrl.startsWith("https://") || baseUrl.startsWith("http://")) {
            "baseUrl must start with http:// or https://."
        }
        require(notificationChannelId.isNotBlank()) { "notificationChannelId must not be blank." }
    }

    internal val normalizedBaseUrl: String
        get() = baseUrl.trimEnd('/')
}
