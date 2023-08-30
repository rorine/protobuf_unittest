package com.example.lib.moss.util.json.internal

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoMessageBuilder
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.any.getMessageBuilder
import com.example.lib.moss.util.any.pack
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.ANY_TYPE_URL
import com.example.lib.moss.util.common.internal.ANY_VALUE
import com.example.lib.moss.util.common.internal.FOR_NUMBER
import com.example.lib.moss.util.common.internal.getMethodOneArg
import com.example.lib.moss.util.common.internal.util.dCheck
import com.example.lib.moss.util.common.types.isBoolean
import com.example.lib.moss.util.common.types.isByteString
import com.example.lib.moss.util.common.types.isDouble
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isFloat
import com.example.lib.moss.util.common.types.isInt
import com.example.lib.moss.util.common.types.isLong
import com.example.lib.moss.util.common.types.isProtoAny
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.common.types.isString
import com.example.lib.moss.util.common.types.newBuilderByMessage
import com.example.lib.moss.util.json.internal.parser.FieldDescriptor
import com.example.lib.moss.util.json.internal.parser.MapFieldDescriptor
import com.example.lib.moss.util.json.internal.parser.RepeatedFieldDescriptor
import com.example.lib.moss.util.json.internal.parser.findField
import com.example.lib.moss.util.json.internal.parser.parseBool
import com.example.lib.moss.util.json.internal.parser.parseBytes
import com.example.lib.moss.util.json.internal.parser.parseDouble
import com.example.lib.moss.util.json.internal.parser.parseFloat
import com.example.lib.moss.util.json.internal.parser.parseInt
import com.example.lib.moss.util.json.internal.parser.parseLong
import com.example.lib.moss.util.json.internal.parser.parseString
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import java.io.StringReader

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/27
 */

internal class ParserImpl(val protoKeyStyle: ProtoKeyStyle, val ignoringUnknownFields: Boolean) {

    fun merge(json: String, builder: ProtoMessageBuilder) {
        val reader = JsonReader(StringReader(json))
        reader.isLenient = false
        merge(JsonParser.parseReader(reader), builder)
    }

    private fun merge(json: JsonElement, builder: ProtoMessageBuilder) {
        // Maybe check well known parsers.
        mergeMessage(json, builder, false)
    }

    /**
     * 递归调用入口.
     */
    private fun mergeMessage(json: JsonElement, builder: ProtoMessageBuilder, skipTypeUrl: Boolean) {
        if (json !is JsonObject) {
            UtilRuntime.e(TAG, "ParserImpl.mergeMessage expect json object but got $json")
            return
        } else {
            // Continue.
        }
        for (entry: Map.Entry<String, JsonElement> in json.entrySet()) {
            if (skipTypeUrl && entry.key == ANY_TYPE_URL) {
                // Skip.
                continue
            } else {
                // Continue.
            }
            if (entry.value is JsonNull) {
                // Ignore null entry value.
                continue
            } else {
                // Continue.
            }

            val field: FieldDescriptor? = findField(entry.key, protoKeyStyle, builder)
            if (field == null) {
                UtilRuntime.e(TAG, "ParserImpl.mergeMessage cannot find field ${entry.key} in message")
                if (ignoringUnknownFields) {
                    continue
                } else {
                    // Stop when field unknown.
                    return
                }
            } else {
                // 填充 field的值.
                mergeField(field, entry.value, builder)
            }
        }
    }

    //region MergeField
    private fun mergeField(field: FieldDescriptor, json: JsonElement, builder: ProtoMessageBuilder) {
        when (field) {
            is RepeatedFieldDescriptor -> mergeRepeatedField(field, json, builder)
            is MapFieldDescriptor -> mergeMapField(field, json, builder)
            else -> mergeSingleField(field, json, builder)
        }
    }

    /*
     * 解析并设置single field.
     */
    private fun mergeSingleField(field: FieldDescriptor, json: JsonElement, builder: ProtoMessageBuilder) {
        try {
            val value = parseJson(json, field.valueClass)
            if (value != null) {
                field.method.invoke(builder, value)
            } else {
                if (isEnum(field.valueClass)) {
                    field.enumRawMethod?.invoke(builder, parseInt(json))
                } else {
                    // Ignore null value.
                    UtilRuntime.e(TAG, "ParserImpl.mergeSingleField parse $json null")
                }
            }
        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            // Ignore and continue.
        }
    }

    /*
     * 解析并设置map field.
     */
    private fun mergeRepeatedField(field: RepeatedFieldDescriptor, json: JsonElement, builder: ProtoMessageBuilder) {
        try {
            if (json !is JsonArray) {
                // Ignore invalid field.
                UtilRuntime.e(TAG, "ParserImpl.mergeRepeatedField expect json array but found $json")
                return
            } else {
                for (jsonItem in json) {
                    val value = parseJson(jsonItem, field.valueClass)
                    if (value != null) {
                        field.method.invoke(builder, value)
                    } else {
                        if (isEnum(field.valueClass)) {
                            field.enumRawMethod?.invoke(builder, parseInt(jsonItem))
                        } else {
                            // Ignore null value.
                            UtilRuntime.e(TAG, "ParserImpl.mergeRepeatedField parse $jsonItem null")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            // Ignore and continue.
        }
    }

    /*
     * 解析并设置repeated field.
     */
    private fun mergeMapField(field: MapFieldDescriptor, json: JsonElement, builder: ProtoMessageBuilder) {
        try {
            if (json !is JsonObject) {
                UtilRuntime.e(TAG, "ParserImpl.mergeMapField expect json object but found $json")
                return

            } else {
                for (entry: Map.Entry<String, JsonElement> in json.entrySet()) {
                    val jsonKey = entry.key
                    val jsonValue = entry.value
                    if (jsonValue is JsonNull) {
                        // Ignore json null.
                        continue
                    } else {
                        val key = parseJson(JsonPrimitive(jsonKey), field.keyClass)
                        val value = parseJson(jsonValue, field.valueClass)
                        if (key != null && value != null) {
                            field.method.invoke(builder, key, value)
                        } else {
                            if (key != null && isEnum(field.valueClass)) {
                                field.enumRawMethod?.invoke(builder, key, parseInt(jsonValue))
                            } else {
                                // Ignore null key value.
                                UtilRuntime.e(TAG, "ParserImpl.mergeMapField parse $jsonKey or $jsonValue null")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            // Ignore and continue.
        }
    }
    //endregion


    //region Parse
    @Throws(InvalidProtocolBufferException::class)
    private fun parseJson(json: JsonElement, clazz: Class<*>): Any? {

        return if (isBoolean(clazz)) {
            parseBool(json)

        } else if (isInt(clazz)) {
            parseInt(json)

        } else if (isLong(clazz)) {
            parseLong(json)

        } else if (isFloat(clazz)) {
            parseFloat(json)

        } else if (isDouble(clazz)) {
            parseDouble(json)

        } else if (isByteString(clazz)) {
            parseBytes(json)

        } else if (isString(clazz)) {
            parseString(json)

        } else if (isEnum(clazz)) {
            parseEnum(json, clazz)

        } else if (isProtoAny(clazz)) {
            parseAny(json, clazz)

        } else if (isProtoMessage(clazz)) {
            parseMessage(json, clazz)

        } else {
            // Unlikely.
            dCheck(false)
            return null
        }
    }

    /*
     * 返回解析后的enum值.
     * 枚举无法简单创建一个instance，所以采用和GPB相同的设置raw value方法.
     */
    private fun parseEnum(json: JsonElement, clazz: Class<*>): Any? {
        return try {
            val value = json.asInt
            val method = getMethodOneArg(FOR_NUMBER, clazz, Int::class.java)
            method?.invoke(null, value)

        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            null
        }
    }

    // 返回解析后的 proto any.
    private fun parseAny(json: JsonElement, clazz: Class<*>): Any? {
        try {
            if (json is JsonObject) {
                val typeUrl = json[ANY_TYPE_URL].asString

                if (typeUrl != null) {
                    val builder = getMessageBuilder(typeUrl) as? ProtoMessageBuilder
                    builder?.let { mergeMessage(json, it, true) }
                    val message = builder?.build()

                    if (message != null) {
                        return pack(message)
                    } else {
                        // 兼容any未解析为json, unlikely by protobuf json protocol.
                        UtilRuntime.e(TAG, "ParserImpl.parseAny parse $json null")
                        val any = ProtoAny.newBuilder()
                        any.typeUrl = typeUrl
                        val encoded = json[ANY_VALUE].asString
                        encoded?.let { any.value = ByteString.copyFrom(UtilRuntime.base64Decode(it)) }
                        return any.build()
                    }
                } else {
                    // Ignore json without typeUrl.
                    UtilRuntime.e(TAG, "ParserImpl.parseAny ignore json any without typeUrl")
                    return null
                }
            } else {
                // Ignore invalid json type.
                UtilRuntime.e(TAG, "ParserImpl.parseAny expect json object but found $json")
                return null
            }
        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            return null
        }
    }

    // 返回解析后的 proto message.
    private fun parseMessage(json: JsonElement, clazz: Class<*>): Any? {
        val builder = newBuilderByMessage(clazz) as? ProtoMessageBuilder
        if (builder != null) {
            mergeMessage(json, builder, false)
            return builder.build()

        } else {
            UtilRuntime.e(TAG, "ParserImpl.parseMessage parse $json new message builder failed")
            return null
        }
    }
    //endregion

}
