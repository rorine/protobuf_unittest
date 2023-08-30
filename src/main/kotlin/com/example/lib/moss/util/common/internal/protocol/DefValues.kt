package com.example.lib.moss.util.common.internal.protocol

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.common.types.isByteStringField
import com.example.lib.moss.util.common.types.isMapLiteField
import com.example.lib.moss.util.common.types.isRepeatedField
import com.google.protobuf.ByteString
import com.google.protobuf.Internal
import com.google.protobuf.MapFieldLite
import java.lang.reflect.Field

/**
 * 描述：判断12种类型是否为默认值.
 *
 * Created by zhangyuling on 2023/6/13
 */

/**
 * 已知类型的默认值.
 */
private val defValue: Map<Class<*>, Any?> = mapOf(
    Int::class.java to 0, // 包括枚举.

    Long::class.java to 0L,
    Boolean::class.java to false,
    Double::class.java to 0.0,
    Float::class.java to 0.0,
    String::class.java to ""
)

/**
 * 字段是否为默认值.
 *
 * see https://developers.google.com/protocol-buffers/docs/proto3#default
 */
internal fun isDefValue(f: Field, message: ProtoMessage): Boolean {
    val value = f.get(message)
    if (value == null) {
        return true
    } else {
        return if (isRepeatedField(f)) {
            isDefRepeated(value)
        } else if (isByteStringField(f)) {
            isDefByteString(value)
        } else if (defValue.containsKey(f.type)) {
            // 精度.
            if (isFloatOrDouble(f.type)) {
                return isDefFloatOrDouble(value, defValue[f.type])
            } else {
                value == defValue[f.type]
            }
        } else if (isMapLiteField(f)) {
            isDefMap(value)
        } else {
            // 其他三种类型(message, any, oneof)值非null.
            false
        }
    }
}

private fun isFloatOrDouble(clazz: Class<*>): Boolean {
    return clazz == Float::class.java || clazz == Double::class.java
}

// 简单比较字符串.
private fun isDefFloatOrDouble(value: Any, defValue: Any?): Boolean {
    return defValue?.let { value.toString() == defValue.toString() } ?: false
}

private fun isDefRepeated(value: Any): Boolean {
    val list = value as? Internal.ProtobufList<*>
    return if (list == null) {
        // Unlikely.
        false
    } else {
        list.size == 0
    }
}

private fun isDefByteString(value: Any): Boolean {
    val bytes = value as? ByteString
    return if (bytes == null) {
        // Unlikely.
        false
    } else {
        bytes.size() == 0
    }
}

/*
 * javalite 生成的map 非null, 是空的.
 * java     生成的map 是null.
 */
private fun isDefMap(value: Any): Boolean {
    val map = value as? MapFieldLite<*, *>
    return if (map == null) {
        // Unlikely.
        false
    } else {
        map.isEmpty()
    }
}