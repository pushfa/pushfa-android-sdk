package com.pushfa.sdk

class PushfaException(
    message: String,
    val httpStatus: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

data class PushfaResult<T> internal constructor(
    val value: T? = null,
    val error: PushfaException? = null,
) {
    val isSuccess: Boolean get() = error == null

    @Suppress("UNCHECKED_CAST")
    fun getOrThrow(): T {
        error?.let { throw it }
        return value as T
    }

    internal companion object {
        fun <T> success(value: T): PushfaResult<T> = PushfaResult(value = value)
        fun <T> failure(error: PushfaException): PushfaResult<T> = PushfaResult(error = error)
    }
}

fun interface PushfaCallback<T> {
    fun onComplete(result: PushfaResult<T>)
}
