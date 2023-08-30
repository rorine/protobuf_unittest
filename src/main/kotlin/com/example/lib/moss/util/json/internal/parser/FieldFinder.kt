package com.example.lib.moss.util.json.internal.parser

import com.example.lib.moss.api.ProtoMessageBuilder
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.fieldRawValueSetterName
import com.example.lib.moss.util.common.internal.fieldSetterName
import com.example.lib.moss.util.common.internal.findAllMethods
import com.example.lib.moss.util.common.internal.getMethodOneArg
import com.example.lib.moss.util.common.internal.getMethodTwoArg
import com.example.lib.moss.util.common.internal.mapFieldPutterName
import com.example.lib.moss.util.common.internal.mapFieldRawValuePutterName
import com.example.lib.moss.util.common.internal.repeatedFieldAdderName
import com.example.lib.moss.util.common.internal.repeatedFieldRawValueAdderName
import com.example.lib.moss.util.common.naming.KeyName
import com.example.lib.moss.util.common.types.isEnum
import com.example.lib.moss.util.common.types.isProtoMessage
import com.example.lib.moss.util.json.internal.TAG
import java.lang.reflect.Method

/**
 * 描述：类似rest中的find field, 但是找的是写方法, 同时调整了返回类型.
 *
 * Created by zhangyuling on 2023/6/27
 */

internal fun findField(jsonKey: String, style: ProtoKeyStyle, builder: ProtoMessageBuilder): FieldDescriptor? {
    //
    try {
        val methodName = KeyName.toJavaMethodName(jsonKey, style)

        var descriptor = doFindSingleFieldDescriptor(methodName, builder)
        if (descriptor != null) {
            return descriptor
        } else {
            // Try repeated field.
        }

        descriptor = doFindRepeatedFieldDescriptor(methodName, builder)
        if (descriptor != null) {
            return descriptor
        } else {
            // Try map field.
        }
        return doFindMapFieldDescriptor(methodName, builder)

    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return null
    }
}

//region SingleField
/**
 * 找到single field descriptor.
 */
private fun doFindSingleFieldDescriptor(methodName: String, builder: ProtoMessageBuilder): FieldDescriptor? {
    val singleFieldSetter = fieldSetterName(methodName)
    val method = findSingleFieldSetterMethod(singleFieldSetter, builder)

    if (method != null) {
        val clazz = method.parameterTypes.first()
        if (isEnum(clazz)) {
            val enumRawValueSetter = fieldRawValueSetterName(methodName)
            val enumRawMethod = getMethodOneArg(enumRawValueSetter, builder, Int::class.java)
            return FieldDescriptor(method, clazz, enumRawMethod)

        } else {
            return FieldDescriptor(method, clazz, null)
        }
    } else {
        return null
    }
}

/*
 * 找到single field setter. 注意对于proto message, setter有两种
 *
 * Builder setFood(com.bapis.example.app.interfaces.v1.Food value)
 * Builder setFood(com.bapis.example.app.interfaces.v1.Food.Builder builderForValue)
 *
 * 考虑和repeated、map接口通用性选择value 这个, 通常就是第一个.
 */
private fun findSingleFieldSetterMethod(singleFieldSetter: String, builder: ProtoMessageBuilder): Method? {
    val methods = findAllMethods(singleFieldSetter, builder).filter { isValidSingleFieldSetter(it) }
    if (methods.isEmpty()) {
        // Unlikely by proto name convention.
        return null

    } else if (methods.size == 1) {
        return methods[0]

    } else {
        // For proto message.
        return methods.firstOrNull { isProtoMessage(it.parameterTypes.first()) }
    }
}

/*
 * Builder setBuilding(int value) 当前仅检查参数个数.
 */
private fun isValidSingleFieldSetter(method: Method): Boolean {
    // Since jvm 1.8.
    // return method.parameterCount == 1
    return method.parameterTypes.size == 1
}
//endregion

//region RepeatedField
/**
 * 找到repeated field descriptor.
 */
private fun doFindRepeatedFieldDescriptor(methodName: String, builder: ProtoMessageBuilder): RepeatedFieldDescriptor? {
    val repeatedFieldAdder = repeatedFieldAdderName(methodName)
    val method = findRepeatedFieldAdderMethod(repeatedFieldAdder, builder)

    if (method != null) {
        val itemClazz = method.parameterTypes.first()
        if (isEnum(itemClazz)) {
            val enumRawValueAdder = repeatedFieldRawValueAdderName(methodName)
            val enumRawMethod = getMethodOneArg(enumRawValueAdder, builder, Int::class.java)
            return RepeatedFieldDescriptor(method, itemClazz, enumRawMethod)

        } else {
            return RepeatedFieldDescriptor(method, itemClazz, null)
        }
    } else {
        return null
    }
}

/*
 * 找到repeated field adder. 注意对于proto message, adder也是有两种, 用value那种.
 */
private fun findRepeatedFieldAdderMethod(repeatedFieldAdder: String, builder: ProtoMessageBuilder): Method? {
    val methods = findAllMethods(repeatedFieldAdder, builder).filter { isValidRepeatedFieldAdder(it) }
    if (methods.isEmpty()) {
        // Unlikely by proto name convention.
        return null

    } else if (methods.size == 1) {
        return methods[0]

    } else {
        // For proto message.
        return methods.firstOrNull { isProtoMessage(it.parameterTypes.first()) }
    }
}

/*
 * Builder addAnotherList(int value) 当前仅检查参数个数.
 */
private fun isValidRepeatedFieldAdder(method: Method): Boolean {
    // Since jvm 1.8.
    // return method.parameterCount == 1
    return method.parameterTypes.size == 1
}
//endregion

//region MapField
/**
 * 找到map field descriptor.
 */
private fun doFindMapFieldDescriptor(methodName: String, builder: ProtoMessageBuilder): MapFieldDescriptor? {
    val mapFieldPutter = mapFieldPutterName(methodName)
    val method = findMapFieldPutterMethod(mapFieldPutter, builder)

    if (method != null) {
        val keyClazz = method.parameterTypes[0]
        val valueClazz = method.parameterTypes[1]
        if (isEnum(valueClazz)) {
            val enumRawValuePutter = mapFieldRawValuePutterName(methodName)
            val enumRawMethod = getMethodTwoArg(enumRawValuePutter, builder, keyClazz, Int::class.java)
            return MapFieldDescriptor(method, keyClazz, valueClazz, enumRawMethod)

        } else {
            return MapFieldDescriptor(method, keyClazz, valueClazz, null)
        }
    } else {
        return null
    }
}

/*
 * 找到map field putter. 注意对于proto message 只有用value一种.
 */
private fun findMapFieldPutterMethod(mapFieldPutter: String, builder: ProtoMessageBuilder): Method? {
    val methods = findAllMethods(mapFieldPutter, builder).filter { isValidMapFieldPutter(it) }
    if (methods.isEmpty()) {
        // Unlikely by proto name convention.
        return null

    } else if (methods.size == 1) {
        return methods[0]

    } else {
        // Unlikely, reserved.
        return methods.firstOrNull { isProtoMessage(it.parameterTypes[1]) }
    }
}

/*
 * Builder putAnotherMap(java.lang.String key, int value) 当前仅检查参数个数.
 */
private fun isValidMapFieldPutter(method: Method): Boolean {
    // Since jvm 1.8.
    // return method.parameterCount == 2
    return method.parameterTypes.size == 2
}
//endregion