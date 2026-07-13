package com.pushfa.sdk.internal

import android.content.Context
import com.pushfa.sdk.PushfaConfig
import com.pushfa.sdk.PushfaState
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

internal class PushfaStorage(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun saveConfig(config: PushfaConfig) {
        preferences.edit()
            .putString(KEY_PUBLIC_KEY, config.apiPublicKey)
            .putString(KEY_BASE_URL, config.normalizedBaseUrl)
            .putBoolean(KEY_AUTO_REGISTER, config.autoRegister)
            .putBoolean(KEY_AUTO_DISPLAY, config.autoDisplayNotifications)
            .putBoolean(KEY_TRACK_VISITS, config.trackVisits)
            .putString(KEY_CHANNEL_ID, config.notificationChannelId)
            .putString(KEY_CHANNEL_NAME, config.notificationChannelName)
            .putInt(KEY_CHANNEL_IMPORTANCE, config.notificationChannelImportance)
            .putInt(KEY_SMALL_ICON, config.smallIconResId)
            .apply {
                if (config.accentColor == null) remove(KEY_ACCENT_COLOR)
                else putInt(KEY_ACCENT_COLOR, config.accentColor)
            }
            .apply()
    }

    fun loadConfig(): PushfaConfig? {
        val publicKey = preferences.getString(KEY_PUBLIC_KEY, null) ?: return null
        return PushfaConfig(
            apiPublicKey = publicKey,
            baseUrl = preferences.getString(KEY_BASE_URL, "https://pushfa.com") ?: "https://pushfa.com",
            autoRegister = preferences.getBoolean(KEY_AUTO_REGISTER, true),
            autoDisplayNotifications = preferences.getBoolean(KEY_AUTO_DISPLAY, true),
            trackVisits = preferences.getBoolean(KEY_TRACK_VISITS, true),
            notificationChannelId = preferences.getString(KEY_CHANNEL_ID, "pushfa_default") ?: "pushfa_default",
            notificationChannelName = preferences.getString(KEY_CHANNEL_NAME, "Pushfa notifications") ?: "Pushfa notifications",
            notificationChannelImportance = preferences.getInt(KEY_CHANNEL_IMPORTANCE, 4),
            smallIconResId = preferences.getInt(KEY_SMALL_ICON, 0),
            accentColor = if (preferences.contains(KEY_ACCENT_COLOR)) preferences.getInt(KEY_ACCENT_COLOR, 0) else null,
        )
    }

    fun subscriberId(): String? = preferences.getString(KEY_SUBSCRIBER_ID, null)

    fun getOrCreateSubscriberId(): String {
        subscriberId()?.let { return it }
        val value = UUID.randomUUID().toString()
        preferences.edit().putString(KEY_SUBSCRIBER_ID, value).apply()
        return value
    }

    fun saveSubscriberId(value: String) {
        preferences.edit().putString(KEY_SUBSCRIBER_ID, value).apply()
    }

    fun pushToken(): String? = preferences.getString(KEY_PUSH_TOKEN, null)

    fun savePushToken(value: String) {
        preferences.edit().putString(KEY_PUSH_TOKEN, value).putBoolean(KEY_HAS_PUSH, true).apply()
    }

    fun setHasPush(value: Boolean) {
        preferences.edit().putBoolean(KEY_HAS_PUSH, value).apply()
    }

    fun saveServerState(data: JSONObject) {
        val editor = preferences.edit()
        data.optString("subscriber_id").takeIf { it.isNotBlank() }?.let {
            editor.putString(KEY_SUBSCRIBER_ID, it)
        }
        if (data.has("has_push")) editor.putBoolean(KEY_HAS_PUSH, data.optBoolean("has_push"))
        if (data.has("alias")) editor.putString(KEY_EXTERNAL_ID, data.optString("alias").takeIf { it.isNotBlank() })
        else editor.remove(KEY_EXTERNAL_ID)
        if (data.has("aliases")) editor.putString(KEY_ALIASES, data.optJSONObject("aliases")?.toString() ?: "{}")
        else editor.remove(KEY_ALIASES)
        if (data.has("topics")) editor.putString(KEY_TOPICS, data.optJSONArray("topics")?.toString() ?: "[]")
        editor.apply()
    }

    fun saveTopics(topics: JSONArray) {
        preferences.edit().putString(KEY_TOPICS, topics.toString()).apply()
    }

    fun saveExternalId(value: String?) {
        preferences.edit().apply {
            if (value.isNullOrBlank()) remove(KEY_EXTERNAL_ID) else putString(KEY_EXTERNAL_ID, value)
        }.apply()
    }

    fun saveAliases(aliases: JSONObject) {
        preferences.edit().putString(KEY_ALIASES, aliases.toString()).apply()
    }

    fun state(): PushfaState {
        val aliasesJson = runCatching { JSONObject(preferences.getString(KEY_ALIASES, "{}") ?: "{}") }.getOrDefault(JSONObject())
        val aliases = buildMap {
            val keys = aliasesJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, aliasesJson.optString(key))
            }
        }
        val topicsJson = runCatching { JSONArray(preferences.getString(KEY_TOPICS, "[]") ?: "[]") }.getOrDefault(JSONArray())
        val topics = (0 until topicsJson.length()).mapNotNull { topicsJson.optString(it).takeIf(String::isNotBlank) }

        return PushfaState(
            subscriberId = getOrCreateSubscriberId(),
            pushToken = pushToken(),
            hasPush = preferences.getBoolean(KEY_HAS_PUSH, false),
            externalId = preferences.getString(KEY_EXTERNAL_ID, null),
            aliases = aliases,
            topics = topics,
        )
    }

    fun lastVisitDate(): String? = preferences.getString(KEY_LAST_VISIT, null)

    fun saveLastVisitDate(value: String) {
        preferences.edit().putString(KEY_LAST_VISIT, value).apply()
    }

    private companion object {
        const val PREFS = "com.pushfa.sdk"
        const val KEY_PUBLIC_KEY = "public_key"
        const val KEY_BASE_URL = "base_url"
        const val KEY_AUTO_REGISTER = "auto_register"
        const val KEY_AUTO_DISPLAY = "auto_display"
        const val KEY_TRACK_VISITS = "track_visits"
        const val KEY_CHANNEL_ID = "channel_id"
        const val KEY_CHANNEL_NAME = "channel_name"
        const val KEY_CHANNEL_IMPORTANCE = "channel_importance"
        const val KEY_SMALL_ICON = "small_icon"
        const val KEY_ACCENT_COLOR = "accent_color"
        const val KEY_SUBSCRIBER_ID = "subscriber_id"
        const val KEY_PUSH_TOKEN = "push_token"
        const val KEY_HAS_PUSH = "has_push"
        const val KEY_EXTERNAL_ID = "external_id"
        const val KEY_ALIASES = "aliases"
        const val KEY_TOPICS = "topics"
        const val KEY_LAST_VISIT = "last_visit"
    }
}
