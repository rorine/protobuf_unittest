package com.example.lib.moss.util.common.internal.protocol

import com.example.lib.moss.util.UtilRuntime
import com.example.lib.moss.util.common.internal.GET_NUMBER
import com.example.lib.moss.util.common.internal.TAG

/**
 * 描述：
 *
 * Created by zhangyuling on 2023/6/14
 */

/**
 * 绕过原始enum背后的int值.
 *
 * 注意oneof内的case枚举并不是 EnumLite, 就是自定义 enum.
 */
internal fun getEnumValueInt(enumValue: Enum<*>): Int {
    return try {
        val method = enumValue::class.java.getMethod(GET_NUMBER)
        method.isAccessible = true

        val intValue = method.invoke(enumValue) as? Int
        intValue ?: 0
    } catch (e: Exception) {
        UtilRuntime.e(TAG, e)
        return 0
    }
}

/**
 * 枚举值转int.
 */
internal fun getEnumValueInt(value: Any): Int {
    val enumValue = value as? Enum<*>
    return enumValue?.let { getEnumValueInt(it) } ?: 0
}