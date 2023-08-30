package com.example.lib.moss.util.test.common

import com.example.lib.moss.util.UtilRuntime
import com.google.common.io.BaseEncoding
import java.net.URLEncoder

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/18
 */

fun initDelegate() {
    UtilRuntime.delegate = object : UtilRuntime.Delegate {

        override fun base64Encode(bytes: ByteArray): String {
            return BaseEncoding.base64().encode(bytes)
        }

        override fun base64Decode(encoded: String): ByteArray {
            return BaseEncoding.base64().decode(encoded)
        }

        override fun urlEscape(value: String): String {
            // @see https://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters
            return URLEncoder.encode(value, "UTF-8").replace("%7E", "~").replace("+", "%20").replace("*", "%2A")
        }

        override fun clazz(fullName: String): String? {
            return Descriptors.clazz(fullName)
        }

        override fun e(tag: String, message: String) {
            println(message)
        }

        override fun e(tag: String, t: Throwable) {
            t.printStackTrace()
        }

        override fun d(tag: String, message: String) {
            println(message)
        }

        override fun fullName(clazz: String): String? {
            return Descriptors.fullName(clazz)
        }

        override val debug: Boolean
            get() = true
    }
}