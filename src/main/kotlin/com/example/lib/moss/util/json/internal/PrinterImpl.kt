package com.example.lib.moss.util.json.internal

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.getListItemClass
import com.example.lib.moss.util.common.internal.getMapEntryValueClass
import com.example.lib.moss.util.common.internal.protocol.isDefValue
import com.example.lib.moss.util.common.internal.protocol.isOneofEnumField
import com.example.lib.moss.util.common.naming.JavaFieldName
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isMapLiteField
import com.example.lib.moss.util.common.types.isOneofField
import com.example.lib.moss.util.common.types.isProtobufField
import com.example.lib.moss.util.common.types.isRepeatedField
import com.example.lib.moss.util.common.types.maybeUnbox
import com.example.lib.moss.util.json.internal.printer.doPrintAny
import com.example.lib.moss.util.json.internal.printer.doPrintBoolean
import com.example.lib.moss.util.json.internal.printer.doPrintByteString
import com.example.lib.moss.util.json.internal.printer.doPrintDouble
import com.example.lib.moss.util.json.internal.printer.doPrintElse
import com.example.lib.moss.util.json.internal.printer.doPrintEnum
import com.example.lib.moss.util.json.internal.printer.doPrintFloat
import com.example.lib.moss.util.json.internal.printer.doPrintInt
import com.example.lib.moss.util.json.internal.printer.doPrintLong
import com.example.lib.moss.util.json.internal.printer.doPrintMaybeInt
import com.example.lib.moss.util.json.internal.printer.doPrintOneof
import com.example.lib.moss.util.json.internal.printer.doPrintString
import com.example.lib.moss.util.json.internal.printer.doPrintTypeUrl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonWriter
import com.google.protobuf.ByteString
import com.google.protobuf.Internal
import com.google.protobuf.MapFieldLite
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Field

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/3
 */

internal const val TAG = "moss.util.json.internal"

internal class PrinterImpl(output: OutputStream, val protoKeyStyle: ProtoKeyStyle) {

    companion object {
        val defaultGson: Gson = GsonBuilder().create()
    }

    val writer = JsonWriter(OutputStreamWriter(output, "UTF-8"))

    // Handle string escapes.
    val gson = defaultGson

    /**
     * 接口方法.
     */
    fun print(message: ProtoMessage, typeUrl: String?) {
        doPrint(message, typeUrl)
        writer.close()
    }

    /**
     * 打印 proto message, 递归调用.
     */
    fun doPrint(message: ProtoMessage, typeUrl: String? = null) {
        writer.beginObject()

        typeUrl?.let { doPrintTypeUrl(it) }

        val clazz = message::class.java
        val fields: Array<Field> = clazz.declaredFields
        for (f in fields) {
            f.isAccessible = true

            if (isProtobufField(f)) {
                doPrintFiled(f, message)
            } else {
                // Skip.
            }
        }
        writer.endObject()
    }

    /**
     * 打印field, 如果非默认值.
     */
    private fun doPrintFiled(f: Field, message: ProtoMessage) {
        if (isDefValue(f, message)) {
            // Ignore default value field.
            return
        } else {
            // Continue.
        }

        if (isOneofEnumField(f, message)) {
            // Ignore enum int in oneof.
            return
        } else {
            // Continue.
        }
        if (isOneofField(f)) {
            // Write concrete json name later.
        } else {
            val key = JavaFieldName.toKeyName(f.name, protoKeyStyle)
            writer.name(key)
        }

        if (isMapLiteField(f)) {
            doPrintMapFieldValue(f, message)
        } else if (isRepeatedField(f)) {
            doPrintRepeatedFieldValue(f, message)
        } else {
            doPrintSingleFieldValue(f, message)
        }
    }

    /**
     * 打印map类型的field.
     */
    private fun doPrintMapFieldValue(f: Field, message: ProtoMessage) {
        writer.beginObject()

        var tried = false
        var realValueType: Class<*>? = null

        (f.get(message) as? MapFieldLite<*, *>)?.forEach {
            val key = it.key
            val value = it.value
            if (key != null && value != null) {
                if (!tried) {
                    realValueType = getMapEntryValueClass(upperCamelCaseField = JavaFieldName.toJavaMethod(f.name), obj = message, key = maybeUnbox(key::class.java))
                    tried = true
                } else {
                    // Skip.
                }
                doPrintMapKey(key)
                doPrintValueWoField(realValueType ?: maybeUnbox(value::class.java), value)
            } else {
                // Ignore null key or value.
            }
        }
        writer.endObject()
    }

    /**
     * 打印list类型的field.
     */
    private fun doPrintRepeatedFieldValue(f: Field, message: ProtoMessage) {
        writer.beginArray()

        val realValueType: Class<*>? = getListItemClass(upperCamelCaseField = JavaFieldName.toJavaMethod(f.name), obj = message)
        (f.get(message) as? Internal.ProtobufList<*>)?.forEach {
            it?.let {
                doPrintValueWoField(realValueType ?: maybeUnbox(it::class.java), it)
            }
        }
        writer.endArray()
    }

    /*
     * key 只能是数值或者字符串类型, 不可以为枚举.
     * @see https://developers.google.com/protocol-buffers/docs/proto3#maps
     */
    private fun doPrintMapKey(key: Any) {
        writer.name(key.toString())
    }

    /**
     * 打印容器中 真实的 value的值.
     * 只处理primitive类型, 如果是容器中wrapper，需要unbox.
     *
     * 仅知道class类型，field不可知, 非message类型都直接print.
     */
    internal fun doPrintValueWoField(clazz: Class<*>, value: Any) {
        when {
            // 简单类型 6种.
            Long::class.java == clazz -> doPrintLong(value as Long)
            // java.lang.Long::class.java == clazz -> doPrintLong(value as Long)
            Boolean::class.java == clazz -> doPrintBoolean(value as Boolean)
            // java.lang.Boolean::class.java == clazz -> doPrintBoolean(value as Boolean)
            Float::class.java == clazz -> doPrintFloat(value as Float)
            // java.lang.Float::class.java == clazz -> doPrintFloat(value as Float)
            Double::class.java == clazz -> doPrintDouble(value as Double)
            // java.lang.Double::class.java == clazz -> doPrintDouble(value as Double)
            String::class.java == clazz -> doPrintString(value as? String)
            ByteString::class.java == clazz -> doPrintByteString(value as? ByteString)

            // 复合类型 4种.
            isEnum(clazz) -> doPrintEnum(value as Int, clazz)
            Int::class.java == clazz -> doPrintInt(value as Int)
            // java.lang.Integer::class.java == clazz -> doPrintInt(value as Int)
            // oneof 不能作为容器的value, 因为oneof修饰field, 并不是类型.
            ProtoAny::class.java == clazz -> doPrintAny(value as? ProtoAny)
            else -> doPrintElse(value as? ProtoMessage)
        }
    }

    /**
     * 打印非容器中的独立 field的值.
     * Maybe 单独处理两个特殊类型, 和 doPrintValueWoField 合并.
     */
    private fun doPrintSingleFieldValue(f: Field, message: ProtoMessage) {

        when (f.type) {
            // 简单类型 6种.
            Long::class.java -> doPrintLong(f.getLong(message))
            Boolean::class.java -> doPrintBoolean(f.getBoolean(message))
            Float::class.java -> doPrintFloat(f.getFloat(message))
            Double::class.java -> doPrintDouble(f.getDouble(message))
            String::class.java -> doPrintString(f.get(message) as? String)
            ByteString::class.java -> doPrintByteString(f.get(message) as? ByteString)

            // 复合类型 4种.
            Int::class.java -> doPrintMaybeInt(f, message)
            Object::class.java -> doPrintOneof(f, message)
            ProtoAny::class.java -> doPrintAny(f.get(message))
            else -> doPrintElse(f.get(message))
        }
    }
}