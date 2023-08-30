package com.example.lib.moss.util.common.types

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/13
 */

fun maybeBox(c: Class<*>): Class<*> {
    return if (c.isPrimitive) c.kotlin.javaObjectType else c
}

fun maybeUnbox(c: Class<*>): Class<*> {
    return c.kotlin.javaPrimitiveType ?: c
}
