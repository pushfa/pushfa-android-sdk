package com.pushfa.sdk

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.pushfa.sdk.internal.NotificationRenderer
import com.pushfa.sdk.internal.PushfaApi
import com.pushfa.sdk.internal.PushfaSdkInfo
import com.pushfa.sdk.internal.PushfaStorage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Pushfa {
    const val VERSION = PushfaSdkInfo.VERSION
    const val DEFAULT_PERMISSION_REQUEST_CODE = 7402

    private val executor = Executors.newFixedThreadPool(2)
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile private var applicationContext: Context? = null
    @Volatile private var config: PushfaConfig? = null
    @Volatile private var storage: PushfaStorage? = null
    @Volatile private var notificationListener: PushfaNotificationListener? = null

    @JvmStatic
    @JvmOverloads
    fun initialize(application: Application, config: PushfaConfig, callback: PushfaCallback<PushfaState>? = null) {
        val context = application.applicationContext
        val localStorage = PushfaStorage(context)
        localStorage.saveConfig(config)
        applicationContext = context
        this.config = config
        storage = localStorage

        execute(callback) {
            ensureSubscriberBlocking()
            if (config.autoRegister && areNotificationsEnabled(context)) {
                val token = Tasks.await(FirebaseMessaging.getInstance().token, 30, TimeUnit.SECONDS)
                registerTokenBlocking(token)
            }
            if (config.trackVisits) notifyVisitBlocking()
            syncStateBlocking()
        }
    }

    @JvmStatic
    fun setNotificationListener(listener: PushfaNotificationListener?) {
        notificationListener = listener
    }

    @JvmStatic
    fun currentState(): PushfaState? = storage?.state()

    @JvmStatic
    fun getSubscriberId(): String? = storage?.getOrCreateSubscriberId()

    @JvmStatic
    fun getPushToken(): String? = storage?.pushToken()

    @JvmStatic
    fun areNotificationsEnabled(context: Context? = applicationContext): Boolean {
        val safeContext = context ?: return false
        if (!NotificationManagerCompat.from(safeContext).areNotificationsEnabled()) return false
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(safeContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    @JvmOverloads
    fun requestNotificationPermission(
        activity: Activity,
        requestCode: Int = DEFAULT_PERMISSION_REQUEST_CODE,
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || areNotificationsEnabled(activity)) return false
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
        return true
    }

    @JvmStatic
    @JvmOverloads
    fun registerForPush(callback: PushfaCallback<PushfaState>? = null) {
        execute(callback) {
            val context = requireContext()
            if (!areNotificationsEnabled(context)) {
                throw PushfaException("Notification permission is not granted.")
            }
            val token = Tasks.await(FirebaseMessaging.getInstance().token, 30, TimeUnit.SECONDS)
            registerTokenBlocking(token)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun syncState(callback: PushfaCallback<PushfaState>? = null) {
        execute(callback) { syncStateBlocking() }
    }

    @JvmStatic
    @JvmOverloads
    fun subscribeTopic(topicUuid: String, callback: PushfaCallback<List<String>>? = null) {
        mutateTopic(topicUuid, "subscribe", callback)
    }

    @JvmStatic
    @JvmOverloads
    fun unsubscribeTopic(topicUuid: String, callback: PushfaCallback<List<String>>? = null) {
        mutateTopic(topicUuid, "unsubscribe", callback)
    }

    @JvmStatic
    @JvmOverloads
    fun getTopics(callback: PushfaCallback<List<String>>? = null) {
        execute(callback) { syncStateBlocking().topics }
    }

    @JvmStatic
    @JvmOverloads
    fun setExternalId(externalId: String?, callback: PushfaCallback<String?>? = null) {
        execute(callback) {
            val runtime = requireRuntime()
            val subscriberId = ensureSubscriberBlocking()
            val parameters = mutableListOf(
                "api_public_key" to runtime.config.apiPublicKey,
                "subscriber_id" to subscriberId,
            )
            val path = if (externalId.isNullOrBlank()) {
                "/api/alias/unset"
            } else {
                parameters += "alias" to externalId
                "/api/alias/set"
            }
            val data = runtime.api.data(runtime.api.post(path, parameters))
            val saved = data.optString("alias").takeIf { it.isNotBlank() }
            runtime.storage.saveExternalId(saved)
            saved
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getExternalId(callback: PushfaCallback<String?>? = null) {
        execute(callback) { syncStateBlocking().externalId }
    }

    @JvmStatic
    @JvmOverloads
    fun addAlias(label: String, value: String, callback: PushfaCallback<Map<String, String>>? = null) {
        addAliases(mapOf(label to value), callback)
    }

    @JvmStatic
    @JvmOverloads
    fun addAliases(aliases: Map<String, String>, callback: PushfaCallback<Map<String, String>>? = null) {
        require(aliases.isNotEmpty()) { "aliases must not be empty." }
        execute(callback) {
            val runtime = requireRuntime()
            val subscriberId = ensureSubscriberBlocking()
            val parameters = mutableListOf(
                "api_public_key" to runtime.config.apiPublicKey,
                "subscriber_id" to subscriberId,
            )
            aliases.forEach { (label, value) -> parameters += "aliases[$label]" to value }
            val data = runtime.api.data(runtime.api.post("/api/aliases/add", parameters))
            val result = data.optJSONObject("aliases") ?: JSONObject()
            runtime.storage.saveAliases(result)
            jsonStringMap(result)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun removeAlias(label: String, callback: PushfaCallback<Map<String, String>>? = null) {
        removeAliases(listOf(label), callback)
    }

    @JvmStatic
    @JvmOverloads
    fun removeAliases(labels: List<String>, callback: PushfaCallback<Map<String, String>>? = null) {
        require(labels.isNotEmpty()) { "labels must not be empty." }
        execute(callback) {
            val runtime = requireRuntime()
            val subscriberId = ensureSubscriberBlocking()
            val parameters = mutableListOf(
                "api_public_key" to runtime.config.apiPublicKey,
                "subscriber_id" to subscriberId,
            )
            labels.forEachIndexed { index, label -> parameters += "labels[$index]" to label }
            val data = runtime.api.data(runtime.api.post("/api/aliases/remove", parameters))
            val result = data.optJSONObject("aliases") ?: JSONObject()
            runtime.storage.saveAliases(result)
            jsonStringMap(result)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getAliases(callback: PushfaCallback<Map<String, String>>? = null) {
        execute(callback) { syncStateBlocking().aliases }
    }

    @JvmStatic
    @JvmOverloads
    fun trackEvent(
        eventName: String,
        params: Map<String, Any?> = emptyMap(),
        aliases: Map<String, String> = emptyMap(),
        callback: PushfaCallback<PushfaEventResult>? = null,
    ) {
        require(eventName.isNotBlank()) { "eventName must not be blank." }
        execute(callback) {
            val runtime = requireRuntime()
            val subscriberId = ensureSubscriberBlocking()
            val parameters = mutableListOf(
                "api_public_key" to runtime.config.apiPublicKey,
                "subscriber_id" to subscriberId,
                "event_name" to eventName,
                "event_time" to isoNow(),
                "idempotency_key" to UUID.randomUUID().toString(),
                "params" to JSONObject(params).toString(),
            )
            aliases.forEach { (label, value) -> parameters += "aliases[$label]" to value }
            val response = runtime.api.post("/api/event", parameters)
            val returnedSubscriberId = response.optString("subscriber_id").takeIf { it.isNotBlank() } ?: subscriberId
            runtime.storage.saveSubscriberId(returnedSubscriberId)
            response.optJSONObject("profile")?.let { profile ->
                val state = JSONObject()
                    .put("subscriber_id", returnedSubscriberId)
                    .put("alias", profile.opt("external_id"))
                    .put("aliases", profile.optJSONObject("aliases") ?: JSONObject())
                    .put("has_push", profile.optBoolean("has_push", runtime.storage.state().hasPush))
                runtime.storage.saveServerState(state)
            }
            PushfaEventResult(eventName, returnedSubscriberId)
        }
    }

    /**
     * Delegate from an app-owned FirebaseMessagingService. Returns false for a
     * non-Pushfa data message so the host can process it normally.
     */
    @JvmStatic
    fun handleMessage(context: Context, remoteMessage: RemoteMessage): Boolean {
        if (!remoteMessage.data.containsKey("id")) return false
        val runtime = runtimeFrom(context) ?: return false
        val message = PushfaMessage.from(remoteMessage) ?: return false
        if (message.rawData["validate_only"].equals("true", ignoreCase = true)) return true
        val handledByHost = runCatching { notificationListener?.onNotificationReceived(message) == true }.getOrDefault(false)
        if (!handledByHost && runtime.config.autoDisplayNotifications) {
            NotificationRenderer.display(context.applicationContext, runtime.config, message)
        }
        return true
    }

    @JvmStatic
    fun displayNotification(context: Context, message: PushfaMessage): Boolean {
        val runtime = runtimeFrom(context) ?: return false
        return NotificationRenderer.display(context.applicationContext, runtime.config, message)
    }

    /** Delegate from an app-owned FirebaseMessagingService.onNewToken(). */
    @JvmStatic
    fun handleNewToken(context: Context, token: String) {
        runtimeFrom(context) ?: return
        if (!areNotificationsEnabled(context)) return
        executor.execute {
            runCatching { registerTokenBlocking(token) }
        }
    }

    private fun mutateTopic(topicUuid: String, operation: String, callback: PushfaCallback<List<String>>?) {
        require(topicUuid.isNotBlank()) { "topicUuid must not be blank." }
        execute(callback) {
            val runtime = requireRuntime()
            val subscriberId = ensureSubscriberBlocking()
            val response = runtime.api.post(
                "/api/topics/syncscribe",
                listOf(
                    "api_public_key" to runtime.config.apiPublicKey,
                    "subscriber_id" to subscriberId,
                    "operation" to operation,
                    "topic" to topicUuid,
                ),
            )
            val topics = runtime.api.data(response).optJSONArray("topics") ?: JSONArray()
            runtime.storage.saveTopics(topics)
            (0 until topics.length()).mapNotNull { topics.optString(it).takeIf(String::isNotBlank) }
        }
    }

    private fun ensureSubscriberBlocking(): String {
        val runtime = requireRuntime()
        val localId = runtime.storage.getOrCreateSubscriberId()
        val parameters = mutableListOf(
            "api_public_key" to runtime.config.apiPublicKey,
            "subscriber_id" to localId,
            "platform" to "android",
            "device" to "mobile",
            "sdk_version" to VERSION,
        )
        runtime.storage.pushToken()?.let { parameters += "token" to it }
        val data = runtime.api.data(runtime.api.post("/api/subscriber/ensure", parameters))
        val serverId = data.optString("subscriber_id").takeIf { it.isNotBlank() }
            ?: throw PushfaException("Pushfa did not return a subscriber ID.")
        runtime.storage.saveSubscriberId(serverId)
        if (data.has("has_push")) runtime.storage.setHasPush(data.optBoolean("has_push"))
        return serverId
    }

    private fun registerTokenBlocking(token: String): PushfaState {
        val runtime = requireRuntime()
        val subscriberId = ensureSubscriberBlocking()
        val oldToken = runtime.storage.pushToken()
        val parameters = mutableListOf(
            "api_public_key" to runtime.config.apiPublicKey,
            "subscriber_id" to subscriberId,
            "token" to token,
            "platform" to "android",
            "device" to "mobile",
            "sdk_version" to VERSION,
        )
        if (!oldToken.isNullOrBlank() && oldToken != token) parameters += "old_token" to oldToken
        val data = runtime.api.data(runtime.api.post("/api/fcm-push-notification-store", parameters))
        data.optString("subscriber_id").takeIf { it.isNotBlank() }?.let(runtime.storage::saveSubscriberId)
        runtime.storage.savePushToken(token)
        return runtime.storage.state()
    }

    private fun syncStateBlocking(): PushfaState {
        val runtime = requireRuntime()
        val subscriberId = ensureSubscriberBlocking()
        val data = runtime.api.data(
            runtime.api.post(
                "/api/subscriber-state",
                listOf(
                    "api_public_key" to runtime.config.apiPublicKey,
                    "subscriber_id" to subscriberId,
                ),
            ),
        )
        runtime.storage.saveServerState(data)
        return runtime.storage.state()
    }

    private fun notifyVisitBlocking() {
        val runtime = requireRuntime()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        if (runtime.storage.lastVisitDate() == today) return
        val subscriberId = ensureSubscriberBlocking()
        runtime.api.post(
            "/api/fcm-push-notification-visit",
            listOf(
                "api_public_key" to runtime.config.apiPublicKey,
                "subscriber_id" to subscriberId,
                "visit_at" to isoNow(),
            ),
        )
        runtime.storage.saveLastVisitDate(today)
    }

    private fun isoNow(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())

    private fun jsonStringMap(json: JSONObject): Map<String, String> = buildMap {
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            put(key, json.optString(key))
        }
    }

    private fun <T> execute(callback: PushfaCallback<T>?, operation: () -> T) {
        executor.execute {
            val result = try {
                PushfaResult.success(operation())
            } catch (error: PushfaException) {
                PushfaResult.failure(error)
            } catch (error: Throwable) {
                PushfaResult.failure(PushfaException(error.message ?: "Pushfa operation failed.", cause = error))
            }
            callback?.let { mainHandler.post { it.onComplete(result) } }
        }
    }

    private fun requireContext(): Context = applicationContext
        ?: throw PushfaException("Call Pushfa.initialize() from Application.onCreate() first.")

    private fun requireRuntime(): Runtime {
        val context = requireContext()
        return runtimeFrom(context)
            ?: throw PushfaException("Call Pushfa.initialize() from Application.onCreate() first.")
    }

    @Synchronized
    private fun runtimeFrom(context: Context): Runtime? {
        val safeContext = context.applicationContext
        val localStorage = storage ?: PushfaStorage(safeContext)
        val localConfig = config ?: localStorage.loadConfig() ?: return null
        applicationContext = safeContext
        storage = localStorage
        config = localConfig
        return Runtime(localConfig, localStorage, PushfaApi(localConfig))
    }

    private data class Runtime(
        val config: PushfaConfig,
        val storage: PushfaStorage,
        val api: PushfaApi,
    )
}
