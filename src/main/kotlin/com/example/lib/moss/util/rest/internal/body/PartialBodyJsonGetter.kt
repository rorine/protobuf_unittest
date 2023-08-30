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
import com.example.lib.moss.util.json.JsonFormat
import com.example.lib.moss.util.rest.internal.FieldInfo
import com.example.lib.moss.util.rest.internal.MapFieldInfo
import com.example.lib.moss.util.rest.internal.RepeatedFieldInfo
import com.example.lib.moss.util.rest.internal.getField
import com.google.protobuf.ByteString
import kotlin.jvm.Throws

/**
 * 描述：指定字段转json, 非递归.
 * 不知道是不是oneof.
 *
 * Created by zhangyuling on 2023/6/7
 */
internal class PartialBodyJsonGetter {

    /**
     * @see https://cloud.google.com/endpoints/docs/grpc-service-config/reference/rpc/google.api#httprule
     */
    @Throws(ProtoUtilException::class)
    fun <T : ProtoMessage> get(protoField: String, req: T): String? {
        val info = getField(protoField, req)

        if (info == null) {
            return null
        } else {
            // Continue.
        }
        return when {
            // Ignore protoField.
            isProtoMessage(info.clazz) -> JsonFormat.printer().print(info.value as ProtoMessage)

            info is RepeatedFieldInfo -> getListJson(protoField, info)
            info is MapFieldInfo -> getMapJson(protoField, info)

            else -> getOtherJson(protoField, info)
        }
    }

    // 使用protoField 作为key.
    private fun getOtherJson(protoField: String, info: FieldInfo): String? {
        val json = getSingleFieldInJson(info.clazz, info.value)
        return json?.let { "{\"$protoField\":$json}" }
    }

    private fun getListJson(protoField: String, info: RepeatedFieldInfo): String {
        val list = (info.value as? List<*>)?.map { it?.run { getSingleFieldInJson(info.itemClazz, it) } }
        val listContent = list?.filterNotNull()?.joinToString(separator = ",") ?: ""
        return "{\"$protoField\":[$listContent]}"
    }

    private fun getMapJson(protoField: String, info: MapFieldInfo): String {
        val list = (info.value as? Map<*, *>)?.map { getMapEntryJson(info.keyClazz, info.valueClazz, it) }
        val listContent = list?.filterNotNull()?.joinToString(separator = ",") ?: ""
        return "{\"$protoField\":{$listContent}}"
    }

    private fun getMapEntryJson(keyClazz: Class<*>, valueClass: Class<*>, entry: Map.Entry<Any?, Any?>): String? {
        if (entry.key == null || entry.value == null) {
            return null
        } else {
            val keyJson = getSingleFieldInJson(keyClazz, entry.key!!, true)
            val valueJson = getSingleFieldInJson(valueClass, entry.value!!)
            if (keyJson != null && valueJson != null) {
                return "$keyJson:$valueJson"
            } else {
                return null
            }
        }
    }

    /**
     * 返回这个value在json中的值, 用于直接拼接字符串. By protos JSON encoding.
     */
    private fun getSingleFieldInJson(clazz: Class<*>, value: Any, asKey: Boolean = false): String? {
        var withQuotes = asKey
        var json: String? = null

        if (clazz.isPrimitive) {
            json = value.toString()
            if (clazz == Long::class.java) {
                // withQuotes = true
            } else {
                // As it was.
            }

        } else if (String::class.java == clazz) {
            json = value.toString()
            withQuotes = true

        } else if (isEnum(clazz)) {
            // json = value.toString()
            // withQuotes = true
            json = getEnumValueInt(value).toString()

        } else if (isProtoAny(clazz)) {
            (value as? ProtoAny)?.let {
                val any = it
                unpack(any)?.let {
                    json = JsonFormat.printer().print(it, any.typeUrl)
                }
            }

        } else if (isByteString(clazz)) {
            (value as? ByteString)?.let { json = UtilRuntime.base64Encode(it.toByteArray()) }
            withQuotes = true

        } else if (isProtoMessage(clazz)) {
            (value as? ProtoMessage)?.let { json = JsonFormat.printer().print(it) }

        } else {
            // Unlikely.
        }

        return if (json == null) {
            null
        } else {
            if (withQuotes) {
                "\"$json\""
            } else {
                json
            }
        }
    }
}