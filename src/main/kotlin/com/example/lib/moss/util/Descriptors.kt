package com.example.lib.moss.util

/**
 * 描述：获取运行时descriptors信息.
 *
 * Created by zhangyuling on 2023/6/5
 */

interface Descriptors {

    fun fullName(clazz: String): String?

    fun clazz(fullName: String): String?
}