package com.example.lib.moss.util.common.naming

import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX_LEN
import com.example.lib.moss.util.common.internal.SNAKE_CASE_SEPARATOR
import com.example.lib.moss.util.common.internal.separateCamelCase
import com.example.lib.moss.util.common.internal.snakeToLowerCamelCase
import com.example.lib.moss.util.common.internal.upperCaseFirstLetter
import java.util.*

/**
 * 描述：Java field name 向其他类型名字转换工具.
 *
 * Created by zhangyuling on 2023/6/18
 */

object JavaFieldName {

    /**
     * lowerCamelName_ to lower_snake_name.
     */
    fun toProtoField(name: String): String {
        return separateCamelCase(name).lowercase().dropLast(FIELD_POSTFIX_LEN)
    }

    /**
     * lowerCamelCase_ to UpperCamelCase.
     */
    fun toJavaMethod(name: String): String {
        return upperCaseFirstLetter(name).dropLast(FIELD_POSTFIX_LEN)
    }

    /**
     * Return json/rest key name with field name.
     */
    fun toKeyName(name: String, style: ProtoKeyStyle): String {
        return when (style) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> separateCamelCase(name, SNAKE_CASE_SEPARATOR).toLowerCase(Locale.ENGLISH).dropLast(FIELD_POSTFIX_LEN)
            ProtoKeyStyle.LOWER_CAMEL_CASE -> name.dropLast(FIELD_POSTFIX_LEN)
        }
    }

    /**
     * Return json/rest key name with oneof enum value name.
     */
    fun toOneofKeyName(enumValue: String, style: ProtoKeyStyle): String {
        return when (style) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> enumValue.toLowerCase(Locale.ENGLISH)
            ProtoKeyStyle.LOWER_CAMEL_CASE -> snakeToLowerCamelCase(enumValue)
        }
    }
}