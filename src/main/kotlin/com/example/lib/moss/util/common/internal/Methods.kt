package com.example.lib.moss.util.common.internal

import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.naming.ProtoMessageClassName
import java.lang.reflect.Method

/**
 * 描述：All method relative code should be here.
 *
 * Created by zhangyuling on 2023/6/13
 */

internal fun fieldGetterName(upperCamelCaseField: String): String {
    return GET + upperCamelCaseField
}

internal fun fieldSetterName(upperCamelCaseField: String): String {
    return SET + upperCamelCaseField
}

internal fun fieldRawValueSetterName(upperCamelCaseField: String): String {
    return SET + upperCamelCaseField + VALUE
}

internal fun repeatedFieldGetterName(upperCamelCaseField: String): String {
    return GET + upperCamelCaseField + LIST
}

internal fun repeatedFieldAdderName(upperCamelCaseField: String): String {
    return ADD + upperCamelCaseField
}

internal fun repeatedFieldRawValueAdderName(upperCamelCaseField: String): String {
    return ADD + upperCamelCaseField + VALUE
}

internal fun mapFieldGetterName(upperCamelCaseField: String): String {
    return GET + upperCamelCaseField + MAP
}

internal fun mapEntryValueGetterName(upperCamelCaseField: String): String {
    return GET + upperCamelCaseField + OR_THROW
}

internal fun mapFieldPutterName(upperCamelCaseField: String): String {
    return PUT + upperCamelCaseField
}

internal fun mapFieldRawValuePutterName(upperCamelCaseField: String): String {
    return PUT + upperCamelCaseField + VALUE
}

/**
 * 返回无参数的method, 如果有.
 */
internal fun getMethodNoArg(name: String, clazz: Class<*>): Method? {
    return try {
        val method = clazz.getDeclaredMethod(name)
        method.isAccessible = true
        method
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

internal fun getMethodNoArg(name: String, obj: Any): Method? {
    return getMethodNoArg(name, obj::class.java)
}

/**
 * 返回带一个指定参数的method, 如果有.
 */
internal fun getMethodOneArg(name: String, clazz: Class<*>, arg: Class<*>): Method? {
    return try {
        val method = clazz.getDeclaredMethod(name, arg)
        method.isAccessible = true
        method
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

internal fun getMethodOneArg(name: String, obj: Any, arg: Class<*>): Method? {
    return getMethodOneArg(name, obj::class.java, arg)
}

/**
 * 返回带两个指定参数的method, 如果有.
 */
internal fun getMethodTwoArg(name: String, clazz: Class<*>, arg1: Class<*>, arg2: Class<*>): Method? {
    return try {
        val method = clazz.getDeclaredMethod(name, arg1, arg2)
        method.isAccessible = true
        method
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

internal fun getMethodTwoArg(name: String, obj: Any, arg1: Class<*>, arg2: Class<*>): Method? {
    return getMethodTwoArg(name, obj::class.java, arg1, arg2)
}


/**
 * 根据指定name找到method.
 */
internal fun findFirstMethod(name: String, clazz: Class<*>): Method? {
    return try {
        val method = clazz.declaredMethods.firstOrNull { it.name == name }
        method?.isAccessible = true
        method
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

internal fun findFirstMethod(name: String, obj: Any): Method? {
    return findFirstMethod(name, obj::class.java)
}

/**
 * 根据指定的name找到所有的methods.
 */
internal fun findAllMethods(name: String, clazz: Class<*>): List<Method> {
    return try {
        val methods = clazz.declaredMethods.filter { it.name == name }
        methods.forEach { it.isAccessible = true }
        methods
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        emptyList()
    }
}

internal fun findAllMethods(name: String, obj: Any): List<Method> {
    return findAllMethods(name, obj::class.java)
}


/**
 * 返回 repeated field中item类型.
 *
 * 输入list field name.
 * 输出已 unbox.
 */
internal fun getListItemClass(upperCamelCaseField: String, obj: Any): Class<*>? {
    return try {
        // getUpperCamelCase(Int)
        val methodName = fieldGetterName(upperCamelCaseField)

        val method = getMethodOneArg(methodName, obj, Int::class.java)
        method?.returnType
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

/**
 * 通过java method，而不是java field，获得list的值.
 */
internal fun getListValue(upperCamelCaseField: String, obj: Any): Any? {
    return try {
        // getUpperCamelCaseList
        val methodName = repeatedFieldGetterName(upperCamelCaseField)

        val method = getMethodNoArg(methodName, obj)
        method?.invoke(obj)
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

/**
 * 通过java method，而不是java field, 获得map的值.
 */
internal fun getMapValue(upperCamelCaseField: String, obj: Any): Any? {
    return try {
        // getUpperCamelCaseMap
        val methodName = mapFieldGetterName(upperCamelCaseField)

        val method = getMethodNoArg(methodName, obj)
        method?.invoke(obj)
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

/**
 * 返回 map field中 entry value的类型
 *
 * 输入key必须严格按照接口定义, 调用方可能需要unbox.
 * 输出已unbox
 */
internal fun getMapEntryValueClass(upperCamelCaseField: String, obj: Any, key: Class<*>): Class<*>? {
    return try {
        // getUpperCamelCaseOrThrow(Key): Value
        val methodName = mapEntryValueGetterName(upperCamelCaseField)

        val method = getMethodOneArg(methodName, obj, key)
        method?.returnType
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

internal typealias MapEntryClass = Pair<Class<*>, Class<*>>

internal fun getMapEntryClass(upperCamelCaseField: String, obj: Any): MapEntryClass? {
    return try {
        // getUpperCamelCaseOrThrow(Key): Value
        val methodName = mapEntryValueGetterName(upperCamelCaseField)
        val method = findFirstMethod(methodName, obj)

        method?.let {
            val keyClass = it.parameterTypes.firstOrNull()
            keyClass?.let {
                return MapEntryClass(it, method.returnType)
            }
        }
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        null
    }
}

