package com.example.lib.moss.util.rest.internal.body

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.any.unpack
import com.example.lib.moss.util.common.internal.protocol.getEnumValueInt
import com.example.lib.moss.util.common.types.isByteString
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isProtoAny
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.exception.ProtoUtilException
import com.example.lib.moss.util.rest.KV
import com.example.lib.moss.util.rest.internal.FieldInfo
import com.example.lib.moss.util.rest.internal.MapFieldInfo
import com.example.lib.moss.util.rest.internal.RepeatedFieldInfo
import com.example.lib.moss.util.rest.internal.TAG
import com.example.lib.moss.util.rest.internal.getField
import com.example.lib.moss.util.rest.internal.toRawString
import com.google.protobuf.ByteString

/**
 * 描述：指定字段转form, 非递归.
 * 不知道是不是oneof.
 *
 * Created by zhangyuling on 2023/6/27
 */

internal class EncodedPartialBodyFormGetter {

    fun <T : ProtoMessage> get(protoField: String, req: T): List<KV>? {
        return try {
            return getForm(protoField, req)

        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            throw ProtoUtilException(e.message, e.cause)
        }
    }

    private fun appendKeyParent(kvs: List<KV>, keyParent: String): List<KV> {
        return kvs.map { KV("$keyParent.${it.first}", it.second) }
    }

    private fun <T : ProtoMessage> getForm(protoField: String, req: T): List<KV>? {
        val info = getField(protoField, req)

        if (info == null) {
            return null
        } else {
            // Continue.
        }

        if (isProtoMessage(info.clazz)) {
            val raw = EncodedBodyForm().get(info.value as ProtoMessage)
            // With proto prefix for form.
            // 如果后端仅支持指向message, 且不带前缀, 那么直接返回raw.
            return raw?.let { appendKeyParent(it, protoField) }

        } else {
            val kvs = mutableListOf<KV>()
            if (info is RepeatedFieldInfo) {
                getListForm(protoField, info, kvs)
            } else if (info is MapFieldInfo) {
                getMapForm(protoField, info, kvs)
            } else {
                getOtherForm(protoField, info, kvs)
            }
            return kvs
        }
    }

    private fun getOtherForm(protoField: String, info: FieldInfo, kvs: MutableList<KV>) {
        getSingleFieldForm(info.clazz, info.value, kvs, protoField)
    }

    private fun getMapForm(protoField: String, info: MapFieldInfo, kvs: MutableList<KV>) {
        (info.value as? Map<*, *>)?.forEach {
            if (it.key == null || it.value == null) {
                return@forEach
            }
            val keyString = toRawString(info.keyClazz, it.key!!)
            if (keyString == null) {
                return@forEach
            }
            val fullKey = UtilRuntime.urlEscape("$protoField[$keyString]")
            getSingleFieldForm(info.valueClazz, it.value!!, kvs, fullKey)
        }
    }

    private fun getListForm(protoField: String, info: RepeatedFieldInfo, kvs: MutableList<KV>) {
        (info.value as? List<*>)?.forEach {
            if (it == null) {
                // Unlikely.
                return@forEach
            }
            getSingleFieldForm(info.itemClazz, it, kvs, protoField)
        }
    }

    private fun getSingleFieldForm(clazz: Class<*>, value: Any, kvs: MutableList<KV>, key: String) {
        if (clazz.isPrimitive) {
            kvs.add(KV(key, value.toString()))

        } else if (clazz == String::class.java) {
            kvs.add(KV(key, UtilRuntime.urlEscape(value.toString())))

        } else if (isEnum(clazz)) {
            // kvs.add(KV(key, value.toString()))
            kvs.add(KV(key, getEnumValueInt(value).toString()))

        } else if (isByteString(clazz)) {
            (value as? ByteString)?.let {
                kvs.add(KV(key, UtilRuntime.urlEscape(UtilRuntime.base64Encode(it.toByteArray()))))
            }

        } else if (isProtoAny(clazz)) {
            (value as? ProtoAny)?.let {
                val any = it
                unpack(any)?.let {
                    val raw = EncodedBodyForm().get(it, any.typeUrl)
                    raw?.let {
                        kvs.addAll(appendKeyParent(it, key))
                    }
                }
            }

        } else if (isProtoMessage(clazz)) {
            (value as? ProtoMessage)?.let {
                val raw = EncodedBodyForm().get(it)
                raw?.let {
                    kvs.addAll(appendKeyParent(it, key))
                }
            }
        } else {
            // Ignore.
        }
    }
}