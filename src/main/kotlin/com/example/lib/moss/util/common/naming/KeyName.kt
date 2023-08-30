package com.example.lib.moss.util.common.naming

import com.example.lib.moss.util.common.ProtoKeyStyle
import com.example.lib.moss.util.common.internal.FIELD_POSTFIX
import com.example.lib.moss.util.common.internal.separateCamelCase
import com.example.lib.moss.util.common.internal.snakeToLowerCamelCase
import com.example.lib.moss.util.common.internal.snakeToUpperCamelCase
import com.example.lib.moss.util.common.internal.upperCaseFirstLetter

/**
 * 描述：Json/rest key name 向其他类型名字转换工具.
 *
 * Created by zhangyuling on 2023/6/13
 */

object KeyName {

    fun toJavaFieldName(keyName: String, keyStyle: ProtoKeyStyle): String {
        return when (keyStyle) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> snakeToLowerCamelCase(keyName).plus(FIELD_POSTFIX)
            ProtoKeyStyle.LOWER_CAMEL_CASE -> keyName.plus(FIELD_POSTFIX)
        }
    }

    fun toJavaMethodName(keyName: String, keyStyle: ProtoKeyStyle): String {
        return when (keyStyle) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> snakeToUpperCamelCase(keyName)
            ProtoKeyStyle.LOWER_CAMEL_CASE -> upperCaseFirstLetter(keyName)
        }
    }

    fun toProtoFieldName(keyName: String, keyStyle: ProtoKeyStyle): String {
        return when (keyStyle) {
            ProtoKeyStyle.LOWER_SNAKE_CASE -> keyName
            ProtoKeyStyle.LOWER_CAMEL_CASE -> separateCamelCase(keyName).lowercase()
        }
    }
}