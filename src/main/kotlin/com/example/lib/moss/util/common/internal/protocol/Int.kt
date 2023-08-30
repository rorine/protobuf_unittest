package com.example.lib.moss.util.common.internal.protocol

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.TAG
import com.example.lib.moss.util.common.internal.fieldGetterName
import com.example.lib.moss.util.common.internal.getMethodNoArg
import com.example.lib.moss.util.common.naming.JavaFieldName
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isProtoEnum
import java.lang.reflect.Field

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/12
 */

internal class ParsedInt(val realType: Class<*>, val readValue: Any, val isInt: Boolean, val isProtoEnum: Boolean, val isOneofEnum: Boolean, val intValue: Int, val StringValue: String)

/**
 * 有三种类型在java field层都保存为 int, 只有在java 接口才知道真实类型.
 *
 * 1. int               接口返回 Int
 * 2. proto enum        接口返回 Enum并且实现了 Internal.EnumLite 接口
 * 3. oneof case        接口返回 Enum，就是普通的枚举.
 */
internal fun parseInt(f: Field, message: ProtoMessage): ParsedInt? {

    try {
        val intValue = f.getInt(message)
        var stringValue = intValue.toString()

        val methodName = fieldGetterName(JavaFieldName.toJavaMethod(f.name))
        val method = getMethodNoArg(methodName, message)

        if (method == null) {
            // Unlikely.
            return null
        } else {
            // Continue.
        }

        val realType = method.returnType
        val realValue = method.invoke(message)
        if (realValue == null) {
            // Unlikely.
            return null
        } else {
            // Continue.
        }

        var isRealInt = false
        var isProtoEnum = false
        var isOneofEnum = false

        if (Int::class.java == realType) {
            isRealInt = true

        } else if (isProtoEnum(realType)) {
            isProtoEnum = true
            // Or maybe call name.
            stringValue = realValue.toString()

        } else if (isEnum(realType)) {
            isOneofEnum = true
            // Or maybe call name.
            stringValue = realValue.toString()

        } else {
            // Unlikely.
            UtilRuntime.e(TAG, "Parse java field raw int to ${realType.canonicalName}")
            return null
        }

        return ParsedInt(realType, realValue, isRealInt, isProtoEnum, isOneofEnum, intValue, stringValue)

    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return null
    }
}