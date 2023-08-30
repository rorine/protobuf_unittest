package com.example.lib.moss.util.rest.internal

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.any.unpack
import com.example.lib.moss.util.common.internal.ANY_TYPE_URL
import com.example.lib.moss.util.common.internal.getListItemClass
import com.example.lib.moss.util.common.internal.getListValue
import com.example.lib.moss.util.common.internal.getMapEntryClass
import com.example.lib.moss.util.common.internal.getMapValue
import com.example.lib.moss.util.common.internal.protocol.getEnumValueInt
import com.example.lib.moss.util.common.internal.protocol.isDefValue
import com.example.lib.moss.util.common.internal.protocol.isOneofEnumField
import com.example.lib.moss.util.common.internal.protocol.parseInt
import com.example.lib.moss.util.common.internal.protocol.parseOneof
import com.example.lib.moss.util.common.naming.JavaFieldName
import com.example.lib.moss.util.common.types.isByteString
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isIntField
import com.example.lib.moss.util.common.types.isMapLiteField
import com.example.lib.moss.util.common.types.isOneofField
import com.example.lib.moss.util.common.types.isProtoAny
import com.example.lib.moss.util.common.types.isProtoAnyField
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.common.types.isProtoMessageField
import com.example.lib.moss.util.common.types.isProtobufField
import com.example.lib.moss.util.common.types.isRepeatedField
import com.example.lib.moss.util.rest.KV
import com.google.protobuf.ByteString
import java.lang.reflect.Field

/**
 * 描述：递归.
 * 知道是不是oneof.
 *
 * Created by zhangyuling on 2023/6/27
 */

internal class KvGetter {

    /**
     * 将req解析为kvs, 忽略其中的 skipFields. 返回的kvs未进行url encode.
     *
     * 遍历所有字段, 用于例如body "*"使用全部字段创建form.
     * BLACKLIST emptyList
     *
     * 遍历除指定字段外其他所有, 用于例如创建query.
     * BLACKLIST someList
     *
     * 遍历指定字段, 用于例如body "a.b"使用特定field创建form.
     * WHITE_LIST someList
     * 返回空, 暂无使用场景.
     * WHITE_LIST emptyList
     * WHITE_LIST 已经移除.
     *
     * 只支持 BLACKLIST, 统一为skipFields.
     *
     */
    @Throws(Exception::class)
    fun <T : ProtoMessage> get(req: T, typeUrl: String?, skipFields: List<String>): List<KV> {
        return try {
            val kvs = mutableListOf<KV>()
            traverse(req, typeUrl, skipFields, kvs, "", "")
            kvs

        } catch (e: Exception) {
            UtilRuntime.e(TAG, e)
            throw e
        }
    }

    private fun shouldSkip(skipFields: List<String>, fullProtoField: String): Boolean {
        return skipFields.contains(fullProtoField)
    }

    /*
     * 是否保留默认值.
     */
    private fun keepDefValue(): Boolean {
        return true
    }

    /**
     * Note that fields which are mapped to URL query parameters must have
     *  a primitive type or
     *  a repeated primitive type or
     *  a non-repeated message type.
     *
     *  In the case of a repeated type, the parameter can be repeated in the URL as ...?param=A&param=B.
     *  In the case of a message type, each field of the message is mapped to a separate parameter, such as ...?foo.a=A&foo.b=B&foo.c=C.
     *
     * Repeated message fields must not be mapped to URL query parameters, because no client library can support such complicated mapping.
     */
    private fun traverse(message: ProtoMessage, typeUrl: String?, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {

        typeUrl?.let { kvs.add(KV(fullKey(ANY_TYPE_URL, keyParent), it)) }

        val clazz = message::class.java
        val fields: Array<Field> = clazz.declaredFields

        for (f in fields) {
            f.isAccessible = true

            if (!isProtobufField(f)) {
                continue
            } else {
                // 注意对于oneof 类型，fullProtoField 是错的.
                // val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)

                if (isDefValue(f, message) && !keepDefValue()) {
                    // 忽略默认值.
                    continue
                }

                // 四种非primitive的类型.
                if (isRepeatedField(f)) {
                    doTraverseList(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                if (isMapLiteField(f)) {
                    doTraverseMap(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                // Any 必须在 proto message前面，因为any是一种proto message类型.
                if (isProtoAnyField(f)) {
                    doTraverseAny(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                if (isProtoMessageField(f)) {
                    doTraverseMessage(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                // 三种特殊类型.
                // 忽略oneof enum (Case_)
                if (isOneofEnumField(f, message)) {
                    continue
                }

                if (isOneofField(f)) {
                    // 此时从java field 获得的proto name是无用的.
                    doTraverseOneof(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                if (isIntField(f)) {
                    doTraverseInt(f, message, skipFields, kvs, parent, keyParent)
                    continue
                }

                // 四种primitive类型.
                doTraverseOthers(f, message, skipFields, kvs, parent, keyParent)
            }
        }
    }

    /*
     * 根据协议, 忽略 item为message的list.
     *
     * 假设
     * message Fruit {
     *  string name = 1;
     *  int64 weight = 2;
     * }
     *
     * 根据google api协议, 映射关系为
     *
     * Fruit one = 1
     * one.name=apple&one.weight=22
     *
     * 根据协议, 映射关系为
     *
     * repeated Fruit list = 2
     * list.name=apple&list.weight=22&list.name=watermelon&list.weight=33
     *
     * map<string, Fruit> dictionary = 3
     * dictionary[22].name=apple&dictionary[22].weight=22&dictionary[33].name=watermelon&dictionary[33].weight=33
     */
    private fun doTraverseList(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }

        val fullKey = fullKey(JavaFieldName.toProtoField(f.name), keyParent)
        val itemClazz = getListItemClass(JavaFieldName.toJavaMethod(f.name), message)
        // if (itemClazz == null || !isValidRepeatedItem(itemClazz)) {
        if (itemClazz == null) {
            // Unlikely, skip.
            return
        } else {
            // (f.get(message) as? List<*>)?.forEach {
            // 对于proto enum返回int数组.
            (getListValue(JavaFieldName.toJavaMethod(f.name), message) as? List<*>)?.forEach {
                if (it == null) {
                    // Unlikely, continue.
                    return@forEach
                }
                if (isProtoAny(itemClazz)) {
                    val any = it as? ProtoAny
                    val proto = any?.let { unpack(it) }
                    proto?.let { traverse(proto, any.typeUrl, skipFields, kvs, fullProtoField, fullKey) }
                    // Ignore unlikely null.

                } else if (isProtoMessage(itemClazz)) {
                    val proto = it as? ProtoMessage
                    proto?.let { traverse(proto, null, skipFields, kvs, fullProtoField, fullKey) }
                    // Ignore unlikely null.

                } else {
                    val valueString = toRawString(itemClazz, it)
                    valueString?.let { kvs.add(KV(fullKey, valueString)) }
                    // Ignore unlikely null.
                }
            }
            // Ignore get repeated value null.
        }
    }

    /*
     * 协议未说明是否支持map. 暂不支持.
     *
     * map<int32, Fruit> fruits
     * 对于Fruit当中的a字段对应为
     * 解析是 parent为 fruits.22.fruit.a, fruits.22.fruit.b (?)
     *
     * 根据自定义协议, 解析为 fruits[22].name, fruits[22].weight
     */
    private fun doTraverseMap(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }
        // 将加入key前缀.
        val partKey = fullKey(JavaFieldName.toProtoField(f.name), keyParent)
        val entryClazz = getMapEntryClass(JavaFieldName.toJavaMethod(f.name), message)
        if (entryClazz == null) {
            // Unlikely, skip.
            return
        } else {
            // (f.get(message) as? MapFieldLite<*, *>)?.forEach{
            // 对于proto enum返回int, 所以通过接口读取map的值.
            (getMapValue(JavaFieldName.toJavaMethod(f.name), message) as? Map<*, *>)?.forEach {

                if (it.key == null || it.value == null) {
                    // Ignore.
                    return@forEach
                }
                val keyString = toRawString(entryClazz.first, it.key!!)
                if (keyString == null) {
                    // Unlikely.
                    return@forEach
                }
                val fullKey = "$partKey[$keyString]"

                val value = it.value!!
                if (isProtoAny(entryClazz.second)) {
                    val any = value as? ProtoAny
                    val proto = any?.let { unpack(it) }
                    proto?.let { traverse(proto, any.typeUrl, skipFields, kvs, fullProtoField, fullKey) }
                    // Ignore unlikely null.

                } else if (isProtoMessage(entryClazz.second)) {
                    val proto = value as? ProtoMessage
                    proto?.let { traverse(proto, null, skipFields, kvs, fullProtoField, fullKey) }
                    // Ignore unlikely null.

                } else {
                    val valueString = toRawString(entryClazz.second, value)
                    valueString?.let { kvs.add(KV(fullKey, valueString)) }
                    // Ignore unlikely null.
                }
            }
            // Ignore get map value null.
        }
    }

    /*
     * On proto message java field.
     */
    private fun doTraverseMessage(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }
        val proto = f.get(message) as? ProtoMessage
        proto?.let { traverse(proto, null, skipFields, kvs, fullProtoField, fullKey(JavaFieldName.toProtoField(f.name), keyParent)) }
        // Maybe null for query/form def value.
    }

    /*
     * On proto any java field.
     */
    private fun doTraverseAny(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }
        val any = f.get(message) as? ProtoAny
        val proto = any?.let { unpack(it) }
        proto?.let { traverse(proto, any.typeUrl, skipFields, kvs, fullProtoField, fullKey(JavaFieldName.toProtoField(f.name), keyParent)) }
        // Maybe null for query/form def value.
    }

    /*
     * On oneof java field.
     */
    private fun doTraverseOneof(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val parsed = parseOneof(f, message)
        if (parsed != null) {
            val fullProtoField = fullProtoField(parsed.realProtoName, parent)
            if (shouldSkip(skipFields, fullProtoField)) {
                return
            }

            val fullKey = fullKey(parsed.realProtoName, keyParent)
            if (isProtoMessage(parsed.realType)) {

                val proto = parsed.realValue as? ProtoMessage
                if (proto != null) {
                    val doubleCheckKvs = mutableListOf<KV>()
                    traverse(parsed.realValue, null, skipFields, doubleCheckKvs, fullProtoField, fullKey)
                    if (doubleCheckKvs.isEmpty()) {
                        // 此时 proto内容是空的，可能是因为skip，或者都是默认值，或者定义是空的.
                        // 为了和没设置oneof 区分开，这个proto填空值.
                        kvs.add(KV(fullKey, ""))
                    } else {
                        kvs.addAll(doubleCheckKvs)
                    }
                } else {
                    // Ignore unlikely null.
                }
            } else {
                doAddKv(parsed.realType, parsed.realValue, kvs, fullKey)
            }
        } else {
            // Ignore unlikely null.
        }
    }

    /*
     * On int java field.
     */
    private fun doTraverseInt(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }

        val fullKey = fullKey(JavaFieldName.toProtoField(f.name), keyParent)
        val parsed = parseInt(f, message)
        if (parsed != null) {
            doAddKv(parsed.realType, parsed.readValue, kvs, fullKey)
        } else {
            // Ignore.
        }
    }

    /*
     * On others.
     */
    private fun doTraverseOthers(f: Field, message: ProtoMessage, skipFields: List<String>, kvs: MutableList<KV>, parent: String, keyParent: String) {
        val fullProtoField = fullProtoField(JavaFieldName.toProtoField(f.name), parent)
        if (shouldSkip(skipFields, fullProtoField)) {
            return
        }
        val fullKey = fullKey(JavaFieldName.toProtoField(f.name), keyParent)
        doAddKv(f.type, f.get(message), kvs, fullKey)
    }

    /*
     * 处理可以直接转换的类型, 并且加入到kvs当中.
     */
    private fun doAddKv(clazz: Class<*>, value: Any?, kvs: MutableList<KV>, key: String) {
        if (value == null) {
            return
        }
        val v = toRawString(clazz, value)
        if (v != null) {
            kvs.add(KV(key, v))
        } else {
            // Ignore.
        }
    }

    /**
     * list的item类型是否支持.
     */
    @Deprecated("扩展google api协议, 需要支持所有类型")
    private fun isValidRepeatedItem(clazz: Class<*>): Boolean {
        return clazz.isPrimitive || clazz == String::class.java || isEnum(clazz) || isByteString(clazz)
    }

    /**
     * 返回完整的proto field name.
     */
    private fun fullProtoField(protoField: String, parent: String): String {
        return if (parent.isEmpty()) protoField else "$parent.$protoField"
    }

    /**
     * 返回完整的key name.
     */
    private fun fullKey(key: String, keyParent: String): String {
        return if (keyParent.isEmpty()) key else "$keyParent.$key"
    }
}

/**
 * 获取支持类型的字符串的值.
 *
 * 这里只处理proto 语义的值.
 */
internal fun toRawString(clazz: Class<*>, value: Any): String? {
    // return if (clazz.isPrimitive || clazz == String::class.java || isEnum(clazz)) {
    return if (clazz.isPrimitive || clazz == String::class.java) {
        value.toString()

    } else if (isEnum(clazz)) {
        getEnumValueInt(value).toString()

    } else if (isByteString(clazz)) {
        (value as? ByteString)?.let { UtilRuntime.base64Encode(it.toByteArray()) }

    } else {
        null
    }
}