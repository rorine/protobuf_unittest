package com.example.lib.moss.util.common.naming

import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX
import com.example.lib.moss.util.common.internal.snakeToLowerCamelCase
import com.example.lib.moss.util.common.internal.snakeToUpperCamelCase

/**
 * 描述：Proto field name 向其他类型名字转换工具.
 *
 * Created by zhangyuling on 2023/6/27
 */

object ProtoFieldName {

    fun toJavaFieldName(name: String): String {
        return snakeToLowerCamelCase(name).plus(FIELD_POSTFIX)
    }

    fun toJavaMethodName(name: String): String {
        return snakeToUpperCamelCase(name)
    }

    fun toKeyName(name: String, style: ProtoKeyStyle): String {
        return when (style) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> name
            ProtoKeyStyle.LOWER_CAMEL_CASE -> snakeToLowerCamelCase(name)
        }
    }

}