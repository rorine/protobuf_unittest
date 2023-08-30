package com.example.lib.moss.util.rest.internal

import com.example.lib.moss.api.ProtoMessage
import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.fieldGetterName
import com.example.lib.moss.util.common.internal.getListItemClass
import com.example.lib.moss.util.common.internal.getMapEntryClass
import com.example.lib.moss.util.common.internal.getMethodNoArg
import com.example.lib.moss.util.common.internal.mapFieldGetterName
import com.example.lib.moss.util.common.internal.repeatedFieldGetterName
import com.example.lib.moss.util.common.naming.ProtoFieldName
import com.example.lib.moss.util.common.types.isList
import com.example.lib.moss.util.common.types.isMap
import java.lang.reflect.Method

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/7
 */

internal const val TAG = "moss.util.rest.internal"

private val DOT = "\\.".toRegex()

/**
 * 根据field name 反射获得对应的类型和值.
 *
 * 根据java生成代码，如果设置返回默认值.
 */
internal fun <T : ProtoMessage> getField(protoField: String, req: T): FieldInfo? {

    val subProtoFields = protoField.split(DOT).toTypedArray()
    if (subProtoFields.isEmpty()) {
        return null
    } else {
        // Continue.
    }

    var currentRoot: Any = req

    var valueProtoField = subProtoFields[0]
    var valueType: Class<*>? = null
    var value: Any? = null

    val fieldCount = subProtoFields.size
    var lastRoot: Any = req

    try {
        for ((index, f) in subProtoFields.withIndex()) {
            valueProtoField = f

            var getter = getter(f, currentRoot)
            // Unboxing.
            valueType = getter.returnType
            value = getter.invoke(currentRoot)

            currentRoot = value

            // Save last object.
            if (index != fieldCount - 1) lastRoot = currentRoot
        }

        if (valueType == null || value == null) {
            return null
        } else {
            // Continue.
        }

        return when {
            isList(valueType) -> getListFieldInfo(valueProtoField, valueType, value, lastRoot)
            isMap(valueType) -> getMapFieldInfo(valueProtoField, valueType, value, lastRoot)
            else -> FieldInfo(valueType, value)
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return null
    }
}

/**
 * 返回protoField对应的getter方法.
 */
@Throws(NoSuchMethodException::class)
private fun getter(protoField: String, obj: Any): Method {
    // TODO 读取 javaField_ 类型, 减少反射错误. 目前(22.9.29) 查询单个field场景极少, 暂不处理.

    var result = fieldGetter(protoField, obj)
    if (result == null) {
        result = repeatedFieldGetter(protoField, obj)
    }
    if (result == null) {
        result = mapFieldGetter(protoField, obj)
    }
    if (result == null) {
        throw NoSuchMethodException("No $protoField getter in ${obj::class.java}")
    }
    return result
}

private fun fieldGetter(protoField: String, obj: Any): Method? {
    val method = fieldGetterName(ProtoFieldName.toJavaMethodName(protoField))
    return getMethodNoArg(method, obj)
}

private fun repeatedFieldGetter(protoField: String, obj: Any): Method? {
    val method = repeatedFieldGetterName(ProtoFieldName.toJavaMethodName(protoField))
    return getMethodNoArg(method, obj)
}

private fun mapFieldGetter(protoField: String, obj: Any): Method? {
    val method = mapFieldGetterName(ProtoFieldName.toJavaMethodName(protoField))
    return getMethodNoArg(method, obj)
}

private fun getListFieldInfo(valueProtoField: String, valueType: Class<*>, value: Any, obj: Any): RepeatedFieldInfo? {
    val itemClazz = getListItemClass(ProtoFieldName.toJavaMethodName(valueProtoField), obj)
    return if (itemClazz != null) {
        RepeatedFieldInfo(valueType, value, itemClazz)
    } else {
        null
    }
}

private fun getMapFieldInfo(valueProtoField: String, valueType: Class<*>, value: Any, obj: Any): MapFieldInfo? {
    val entryClass = getMapEntryClass(ProtoFieldName.toJavaMethodName(valueProtoField), obj)
    return if (entryClass != null) {
        MapFieldInfo(valueType, value, entryClass.first, entryClass.second)
    } else {
        null
    }
}

internal open class FieldInfo(val clazz: Class<*>, val value: Any)

internal class RepeatedFieldInfo(clazz: Class<*>, value: Any, val itemClazz: Class<*>) : FieldInfo(clazz, value)

internal class MapFieldInfo(clazz: Class<*>, value: Any, val keyClazz: Class<*>, val valueClazz: Class<*>) :
    FieldInfo(clazz, value)