package com.pushfa.sdk.internal

import com.pushfa.sdk.PushfaConfig
import com.pushfa.sdk.PushfaException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal class PushfaApi(private val config: PushfaConfig) {
    fun post(path: String, parameters: List<Pair<String, String>>): JSONObject {
        val body = parameters.joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }
        val connection = (URL(config.normalizedBaseUrl + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 20_000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            setRequestProperty("User-Agent", "Pushfa-Android/${PushfaSdkInfo.VERSION}")
        }

        return try {
            connection.outputStream.use { it.write(body.toByteArray(StandardCharsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            val response = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
            if (status !in 200..299 || response.optBoolean("errors", false)) {
                throw PushfaException(
                    response.optString("message").takeIf { it.isNotBlank() } ?: "Pushfa request failed (HTTP $status).",
                    status,
                )
            }
            response
        } catch (error: PushfaException) {
            throw error
        } catch (error: Exception) {
            throw PushfaException("Could not connect to Pushfa.", cause = error)
        } finally {
            connection.disconnect()
        }
    }

    fun data(response: JSONObject): JSONObject = response.optJSONObject("data") ?: JSONObject()

    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8.name())
}

internal object PushfaSdkInfo {
    const val VERSION = "2.0.0"
}
