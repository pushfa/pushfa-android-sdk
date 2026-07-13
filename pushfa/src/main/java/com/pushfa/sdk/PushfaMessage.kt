package com.pushfa.sdk

import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject

data class PushfaNotificationAction(
    val id: String,
    val title: String,
    val url: String?,
)

data class PushfaMessage(
    val id: String,
    val title: String,
    val body: String?,
    val url: String?,
    val imageUrl: String?,
    val iconUrl: String?,
    val badgeUrl: String?,
    val deliveryAckUrl: String?,
    val clickAckUrl: String?,
    val collapseId: String?,
    val silent: Boolean,
    val additionalData: Map<String, Any?>,
    val actions: List<PushfaNotificationAction>,
    val rawData: Map<String, String>,
) {
    internal companion object {
        fun from(remoteMessage: RemoteMessage): PushfaMessage? {
            val data = remoteMessage.data
            val id = data["id"]?.takeIf { it.isNotBlank() } ?: return null
            val notification = remoteMessage.notification
            val title = data["title"] ?: notification?.title.orEmpty()
            val actions = parseActions(data)

            return PushfaMessage(
                id = id,
                title = title,
                body = data["body"] ?: notification?.body,
                url = data["url"] ?: notification?.link?.toString(),
                imageUrl = data["image"] ?: notification?.imageUrl?.toString(),
                iconUrl = data["icon"],
                badgeUrl = data["badge"],
                deliveryAckUrl = data["ackUrl"],
                clickAckUrl = data["clickAckUrl"],
                collapseId = data["collapse_id"] ?: data["tag"],
                silent = data["silent"].equals("true", ignoreCase = true),
                additionalData = parseObject(data["additional_data"]),
                actions = actions,
                rawData = data.toMap(),
            )
        }

        private fun parseActions(data: Map<String, String>): List<PushfaNotificationAction> {
            val raw = data["actions"] ?: return emptyList()
            return runCatching {
                val array = JSONArray(raw)
                buildList {
                    for (index in 0 until array.length()) {
                        val item = array.optJSONObject(index) ?: continue
                        val id = item.optString("action").takeIf { it.isNotBlank() } ?: continue
                        val title = item.optString("title").takeIf { it.isNotBlank() } ?: continue
                        val url = when (id) {
                            "btn-left" -> data["btnLeftUrl"]
                            "btn-right" -> data["btnRightUrl"]
                            else -> item.optString("url").takeIf { it.isNotBlank() }
                        }
                        add(PushfaNotificationAction(id, title, url))
                    }
                }
            }.getOrDefault(emptyList())
        }

        private fun parseObject(raw: String?): Map<String, Any?> {
            if (raw.isNullOrBlank()) return emptyMap()
            return runCatching { jsonObjectToMap(JSONObject(raw)) }.getOrDefault(emptyMap())
        }

        private fun jsonObjectToMap(json: JSONObject): Map<String, Any?> = buildMap {
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, jsonValue(json.opt(key)))
            }
        }

        private fun jsonValue(value: Any?): Any? = when (value) {
            null, JSONObject.NULL -> null
            is JSONObject -> jsonObjectToMap(value)
            is JSONArray -> (0 until value.length()).map { jsonValue(value.opt(it)) }
            else -> value
        }
    }
}

fun interface PushfaNotificationListener {
    /** Return true when the host app handled display itself. */
    fun onNotificationReceived(message: PushfaMessage): Boolean
}
