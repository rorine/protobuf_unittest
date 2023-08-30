package com.example.lib.moss.util

/**
 * 描述：Runtime delegate.
 *
 * Created by zhangyuling on 2023/6/5
 */

object UtilRuntime {

    lateinit var delegate: Delegate

    interface Delegate : Descriptors {
        // Log.
        fun e(tag: String, message: String)
        fun e(tag: String, t: Throwable)
        fun d(tag: String, message: String)

        // For proto byte string json encoding.
        fun base64Encode(bytes: ByteArray): String

        // For proto bytes string json decoding.
        fun base64Decode(encoded: String): ByteArray

        // For rest query encoding.
        fun urlEscape(value: String): String

        val debug: Boolean
    }

    fun e(tag: String, message: String) {
        delegate.e(tag, message)
    }

    fun e(tag: String, t: Throwable) {
        delegate.e(tag, t)
    }

    fun d(tag: String, message: String) {
        delegate.d(tag, message)
    }

    fun base64Encode(bytes: ByteArray): String {
        return delegate.base64Encode(bytes)
    }

    fun base64Decode(encoded: String): ByteArray {
        return delegate.base64Decode(encoded)
    }

    fun fullName(clazz: String): String? {
        return delegate.fullName(clazz)
    }

    fun clazz(fullName: String): String? {
        return delegate.clazz(fullName)
    }

    fun urlEscape(value: String): String {
        return delegate.urlEscape(value)
    }

    fun isDebug(): Boolean {
        return delegate.debug
    }
}