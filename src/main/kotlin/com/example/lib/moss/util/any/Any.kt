package com.example.lib.moss.util.any

import com.example.lib.moss.api.ProtoAny
import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.types.newBuilderByMessage
import com.example.lib.moss.util.json.internal.TAG
import com.google.protobuf.InvalidProtocolBufferException

/**
 * 描述：Missing Any facilities.
 *
 * "Lite" library is much smaller than the full library,
 * and is more appropriate for resource-constrained systems such as mobile phones,
 * which only contains a subset of the methods of Message.
 * In particular, it does not support descriptors or reflection.
 *
 * Created by zhangyuling on 2019-09-17
 */

const val TYPE_URL_PREFIX = "type.googleapis.com"

fun <T : ProtoMessage> pack(message: T): ProtoAny {
    return ProtoAny.newBuilder()
        .setTypeUrl(getTypeUrl(TYPE_URL_PREFIX, message))
        .setValue(message.toByteString())
        .build()
}

/**
 * 先检查type url, 然后解析proto any.
 */
@Throws(InvalidProtocolBufferException::class)
fun <T : ProtoMessage> unpack(any: ProtoAny, clazz: Class<T>): T {
    if (!`is`<T>(any, clazz)!!) {
        throw InvalidProtocolBufferException("Type of the Any message does not match the given class.")
    }
    val defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz)
    val result = defaultInstance.getParserForType().parseFrom(any.getValue()) as T
    return result
}

/**
 * 直接解析proto any, 不使用type url检查, since 7.39.
 */
@Throws(Exception::class)
fun <T : ProtoMessage> unpackSansTypeUrl(any: ProtoAny, clazz: Class<T>): T {
    val defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz)
    val result = defaultInstance.getParserForType().parseFrom(any.getValue()) as T
    return result
}


fun <T : ProtoMessage> `is`(any: ProtoAny, clazz: Class<T>): Boolean {
    val defaultInstance = com.google.protobuf.Internal.getDefaultInstance(clazz)
    return getTypeNameFromTypeUrl(typeUrl = any.typeUrl) == UtilRuntime.fullName(defaultInstance.javaClass.canonicalName!!)
}

/*
 * Using descriptor info generated by moss plugin.
 */
fun <T : ProtoMessage> getTypeUrl(typeUrlPrefix: String, message: T): String {
    val clazz = message.javaClass.canonicalName
    return if (typeUrlPrefix.endsWith("/"))
        typeUrlPrefix + UtilRuntime.fullName(clazz!!)
    else
        typeUrlPrefix + "/" + UtilRuntime.fullName(clazz!!)
}

fun getTypeNameFromTypeUrl(typeUrl: String): String {
    val pos = typeUrl.lastIndexOf('/')
    return if (pos == -1) "" else typeUrl.substring(pos + 1)
}

/**
 * 直接解析proto any, 根据proto message <-> java class找到要解析的java model类型.
 */
fun unpack(any: ProtoAny): ProtoMessage? {
    val protoName = getTypeNameFromTypeUrl(any.typeUrl)
    val clazzName = UtilRuntime.clazz(protoName)

    try {
        val clazz = Class.forName(clazzName) as? Class<out ProtoMessage>

        return if (clazz == null) {
            null
        } else {
            // Parser proto message.
            unpack(any, clazz)
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return null
    }
}

/**
 * 对应 + (nullable GPBMessage *)getObjcMessage:(nonnull NSString *)typeUrl;
 */
fun getMessageBuilder(typeUrl: String): Any? {
    return try {
        val protoName = getTypeNameFromTypeUrl(typeUrl)
        val clazzName = UtilRuntime.clazz(protoName)
        clazzName?.let {
            val clazz = Class.forName(clazzName)
            newBuilderByMessage(clazz)
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }

}