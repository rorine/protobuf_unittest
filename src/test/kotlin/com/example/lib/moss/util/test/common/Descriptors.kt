package com.example.lib.moss.util.test.common

/**
 * 描述：Mock runtime descriptors.
 *
 * Created by zhangyuling on 2023/6/18
 */

object Descriptors {

    fun fullName(clazz: String): String? {
        return when (clazz) {
            "com.bapis.example.app.interfaces.v1.Time" -> "example.app.interface.v1.Time"
            "com.bapis.example.app.interfaces.v1.Weather" -> "example.app.interface.v1.Weather"
            else -> null
        }
    }

    fun clazz(fullName: String): String? {
        return when (fullName) {
            "example.app.interface.v1.Time" -> "com.bapis.example.app.interfaces.v1.Time"
            "example.app.interface.v1.Weather" -> "com.bapis.example.app.interfaces.v1.Weather"
            else -> null
        }
    }
}