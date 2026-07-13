package com.pushfa.sdk

data class PushfaState(
    val subscriberId: String,
    val pushToken: String?,
    val hasPush: Boolean,
    val externalId: String?,
    val aliases: Map<String, String>,
    val topics: List<String>,
)

data class PushfaEventResult(
    val eventName: String,
    val subscriberId: String,
)
