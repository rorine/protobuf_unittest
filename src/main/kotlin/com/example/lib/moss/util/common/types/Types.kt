package com.example.lib.moss.util.common.types

/**
 * 描述：protobuf 生成 java 代码协议.
 *
 * Created by zhangyuling on 2023/6/13
 */

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoAnyBuilder
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX
import com.example.lib.moss.util.common.internal.isKnownClass
import com.example.lib.moss.util.common.internal.isKnownDerivedClass
import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.Internal
import com.google.protobuf.MapFieldLite
import java.lang.reflect.Field


/**
 * 是否为scalar type.
 * bool/12种数值/bytes/string
 */
fun isInt(clazz: Class<*>): Boolean {
    return Int::class.java == clazz
}

fun isBoolean(clazz: Class<*>): Boolean {
    return Boolean::class.java == clazz
}

fun isLong(clazz: Class<*>): Boolean {
    return Long::class.java == clazz
}

fun isFloat(clazz: Class<*>): Boolean {
    return Float::class.java == clazz
}

fun isDouble(clazz: Class<*>): Boolean {
    return Double::class.java == clazz
}

fun isString(clazz: Class<*>): Boolean {
    return String::class.java == clazz
}

/**
 * 是否为protobuf中字段.
 */
fun isProtobufField(f: Field): Boolean {
    // Check valid name and skip generated converter_ field.
    return f.name.endsWith(FIELD_POSTFIX) && (isKnownClass(f.type) || isKnownDerivedClass(f.type))
}

/**
 * 根据class判断是否为子类.
 */
fun isProtoMessage(clazz: Class<*>): Boolean {
    return GeneratedMessageLite::class.java.isAssignableFrom(clazz)
}

/**
 * 是否为proto message builder.
 */
fun isProtoMessageBuilder(clazz: Class<*>): Boolean {
    return GeneratedMessageLite.Builder::class.java.isAssignableFrom(clazz)
}

// 生成field MapFieldLite 类型, 不是其子类.
fun isMapLite(clazz: Class<*>): Boolean {
    return MapFieldLite::class.java.isAssignableFrom(clazz)
}

// getter 返回类型.
fun isMap(clazz: Class<*>): Boolean {
    return Map::class.java == clazz
}

// repeated 生成field 类型.
fun isRepeated(clazz: Class<*>): Boolean {
    return Internal.ProtobufList::class.java.isAssignableFrom(clazz)
}

// repeated 接口类型.
fun isList(clazz: Class<*>): Boolean {
    return List::class.java.isAssignableFrom(clazz)
}

// 直接生成field就是ByteString类型, 不是其子类.
fun isByteString(clazz: Class<*>): Boolean {
    return ByteString::class.java == clazz
}

// oneof case接口类型.
fun isEnum(clazz: Class<*>): Boolean {
    return Enum::class.java.isAssignableFrom(clazz)
}

fun isEnumLite(clazz: Class<*>): Boolean {
    return Internal.EnumLite::class.java.isAssignableFrom(clazz)
}

/**
 * enum 接口类型.
 *
 * 仅判断clazz, 因为实际field都是 int.
 */
fun isProtoEnum(clazz: Class<*>): Boolean {
    return isEnum(clazz) && isEnumLite(clazz)
}

fun isProtoAny(clazz: Class<*>): Boolean {
    return ProtoAny::class.java == clazz
}

fun isProtoAnyBuilder(clazz: Class<*>): Boolean {
    return ProtoAnyBuilder::class.java == clazz
}

/**
 * 是否特定类型的 field.
 */
fun isMapLiteField(f: Field): Boolean {
    return isMapLite(f.type)
}

fun isRepeatedField(f: Field): Boolean {
    return isRepeated(f.type)
}

fun isOneofField(f: Field): Boolean {
    return Object::class.java == f.type
}

fun isByteStringField(f: Field): Boolean {
    return isByteString(f.type)
}

fun isProtoAnyField(f: Field): Boolean {
    return isProtoAny(f.type)
}

fun isProtoMessageField(f: Field): Boolean {
    return isProtoMessage(f.type)
}

fun isIntField(f: Field): Boolean {
    return isInt(f.type)
}