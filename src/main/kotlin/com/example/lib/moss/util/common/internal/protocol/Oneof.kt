package com.example.lib.moss.util.common.internal.protocol

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX_LEN
import com.example.lib.moss.util.common.internal.GET
import com.example.lib.moss.util.common.internal.ONEOF_CASE_POSTFIX
import com.example.lib.moss.util.common.internal.ONEOF_CASE_POSTFIX_LEN
import com.example.lib.moss.util.common.internal.TAG
import com.example.lib.moss.util.common.internal.fieldGetterName
import com.example.lib.moss.util.common.internal.getMethodNoArg
import com.example.lib.moss.util.common.internal.snakeToUpperCamelCase
import com.example.lib.moss.util.common.naming.JavaFieldName
import com.example.lib.moss.util.common.types.isOneofField
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/14
 */

/**
 * 这个field是否为oneof生成的case枚举.
 */
internal fun isOneofEnumField(f: Field, message: ProtoMessage): Boolean {

    if (precheckIsOneofEnumField(f)) {
        // Continue.
    } else {
        return false
    }

    val objName = oneofEnumFieldToObjectFieldName(f.name)

    return try {
        val field = message::class.java.getDeclaredField(objName)
        field.isAccessible = true
        isOneofField(field)
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        false
    }
}

private fun precheckIsOneofEnumField(f: Field): Boolean {
    return (f.type == Int::class.java) && (f.name.endsWith(ONEOF_CASE_POSTFIX))
}

/*
 * eventCase_ -> event_, intOneofCase_ -> intOneof_
 */
private fun oneofEnumFieldToObjectFieldName(enumName: String): String {
    return enumName.dropLast(ONEOF_CASE_POSTFIX_LEN) + FIELD_POSTFIX
}

internal class ParsedOneof(val realProtoName: String, val realType: Class<*>, val realValue: Any, val enumValue: String)

/**
 * 根据 java field 解析oneof 类型.
 *
 * oneof修饰field，反射检查真实的值, 如果有.
 *
 * see https://developers.google.com/protocol-buffers/docs/proto3#oneof
 */
internal fun parseOneof(f: Field, message: ProtoMessage): ParsedOneof? {
    try {
        if (f.get(message) == null) {
            // Maybe null for query/form def value.
            return null
        } else {
            // Continue.
        }

        val enumValue = getOneofEnumValue(f.name, message)
        if (enumValue == null) {
            // Unlikely.
            return null
        } else {
            // Continue.
        }

        val enumInt = getEnumValueInt(enumValue)
        if (enumInt == 0) {
            // Unlikely, unset oneof.
            return null
        } else {
            // Continue.
        }

        val realProtoName = enumValue.name.toLowerCase()
        val method = getRealGetterMethod(enumValue.name, message)
        if (method == null) {
            // Unlikely by name conventions.
            return null
        } else {
            // Continue.
        }

        val realType = method.returnType
        val realValue = method.invoke(message)
        if (realValue == null) {
            // Unlikely by java api.
            return null
        } else {
            return ParsedOneof(realProtoName, realType, realValue, enumValue.name)
        }

    } catch (e: Exception) {
        return null
    }
}

/**
 * 返回这个object对应的枚举值.
 */
private fun getOneofEnumValue(objName: String, message: ProtoMessage): Enum<*>? {
    val enumName = objectFieldToEnumFieldName(objName)
    return try {
        val methodName = fieldGetterName(JavaFieldName.toJavaMethod(enumName))
        val method = getMethodNoArg(methodName, message)
        return method?.invoke(message) as? Enum<*>
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

/*
 * event_ -> eventCase_, intOneof_ -> intOneofCase_
 */
private fun objectFieldToEnumFieldName(objName: String): String {
    return objName.dropLast(FIELD_POSTFIX_LEN) + ONEOF_CASE_POSTFIX
}

/**
 * 获得实际的get方法.
 */
private fun getRealGetterMethod(enumValueName: String, message: ProtoMessage): Method? {
    return try {
        val method = message::class.java.getMethod(oneofFieldGetterName(enumValueName))
        method.isAccessible = true
        method
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

private fun oneofFieldGetterName(enumValueName: String): String {
    return GET + snakeToUpperCamelCase(enumValueName, FIELD_POSTFIX)
}
