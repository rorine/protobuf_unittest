package com.example.lib.moss.util.rest.internal.path

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.common.internal.protocol.getEnumValueInt
import com.example.lib.moss.util.common.types.isByteString
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isList
import com.example.lib.moss.util.common.types.isMap
import com.example.lib.moss.util.common.types.isProtoAny
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.rest.internal.getField

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/7
 */
internal class PathStringGetter {

    /**
     * The path template may refer to one or more fields in the gRPC request message,
     * as long as each field is a non-repeated field with a primitive (non-message) type.
     *
     * @see https://cloud.google.com/endpoints/docs/grpc-service-config/reference/rpc/google.api#grpc-transcoding
     */
    fun <T : ProtoMessage> get(protoField: String, req: T): String? {
        val info = getField(protoField, req)
        if (info == null) {
            return null
        } else {
            // Continue.
        }

        return if (isValidType(info.clazz)) {
            getValueString(info.clazz, info.value)
        } else {
            // Skip invalid type
            null
        }
    }

    private fun getValueString(clazz: Class<*>, value: Any): String? {
        return when {
            // Unboxing.
            clazz.isPrimitive -> value.toString()
            String::class.java == clazz -> value.toString()
            // isEnum(clazz) -> value.toString()
            isEnum(clazz) -> getEnumValueInt(value).toString()

            // Unlikely.
            else -> null
        }
    }

    private fun isValidType(clazz: Class<*>): Boolean {
        return !isProtoMessage(clazz) && !isList(clazz) && !isMap(clazz) && !isByteString(clazz) && !isProtoAny(clazz)
    }
}