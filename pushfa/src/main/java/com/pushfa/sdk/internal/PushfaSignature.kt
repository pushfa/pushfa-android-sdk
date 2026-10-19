package com.pushfa.sdk.internal

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Verifies the `sign` field Pushfa adds to every data message for Android SDK
 * recipients, normal and lite alike.
 *
 * The server signs every non-empty data field together with the recipient FCM
 * token (ECDSA P-256 / SHA-256). A changed message, or a message copied to
 * another device, therefore fails verification and is never displayed.
 */
internal object PushfaSignature {
    private const val FIELD = "sign"
    private const val DOMAIN = "pushfa-sign-v1"

    // Pushfa signing public keys (X.509, Base64). Ship a new key in a release
    // before the server starts signing with it.
    private val PUBLIC_KEYS = listOf(
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEkQr5KqFJ/6WT1WZbHC3jnaP918nWoF6pkzOHWQZWqSReOt6c8NZDxSOsltviJ/Cqpc46DPEx8Ug+7WT0S5qIiw==",
    )

    private val publicKeys: List<PublicKey> by lazy {
        PUBLIC_KEYS.mapNotNull { key ->
            runCatching {
                KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)))
            }.getOrNull()
        }
    }

    fun isValid(data: Map<String, String>, token: String?): Boolean {
        val signature = data[FIELD]?.takeIf { it.isNotBlank() } ?: return false
        if (token.isNullOrBlank()) return false
        val signatureBytes = runCatching { Base64.decode(signature, Base64.DEFAULT) }.getOrNull() ?: return false
        val payload = payload(data, token)
        return publicKeys.any { key ->
            runCatching {
                Signature.getInstance("SHA256withECDSA").run {
                    initVerify(key)
                    update(payload)
                    verify(signatureBytes)
                }
            }.getOrDefault(false)
        }
    }

    /** Must stay byte-identical to App\Services\AndroidPushSignature::payload() on the server. */
    private fun payload(data: Map<String, String>, token: String): ByteArray {
        val output = ByteArrayOutputStream()
        fun part(value: String) {
            val bytes = value.toByteArray(Charsets.UTF_8)
            output.write("${bytes.size}:".toByteArray(Charsets.UTF_8))
            output.write(bytes)
            output.write(','.code)
        }
        part(DOMAIN)
        part(token)
        data.filter { (key, value) -> key != FIELD && value.isNotEmpty() }
            .toSortedMap()
            .forEach { (key, value) ->
                part(key)
                part(value)
            }
        return output.toByteArray()
    }
}
