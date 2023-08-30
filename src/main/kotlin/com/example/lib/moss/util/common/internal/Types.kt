package com.example.lib.moss.util.common.internal

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.util.common.types.isMapLite
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.common.types.isRepeated
import com.google.protobuf.ByteString

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/17
 */

/**
 * 是否为 message中可能出现的已知类型, 9种.
 */
internal fun isKnownClass(clazz: Class<*>): Boolean {
    return when (clazz) {
        Long::class.java,
        Boolean::class.java,
        Float::class.java,
        Double::class.java,
        String::class.java,
        ByteString::class.java,

        Int::class.java,
        Object::class.java,
        ProtoAny::class.java -> true
        else -> false
    }
}

/**
 * 是否 message中可能出现的子类, 3种.
 */
internal fun isKnownDerivedClass(clazz: Class<*>): Boolean {
    return isProtoMessage(clazz) || isMapLite(clazz) || isRepeated(clazz)
}