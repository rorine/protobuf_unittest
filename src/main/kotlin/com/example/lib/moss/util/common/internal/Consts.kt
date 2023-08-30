package com.example.lib.moss.util.common.internal

/**
 * 描述：生成代码中使用的常量.
 *
 * Created by zhangyuling on 2023/6/14
 */

internal const val TAG = "moss.util.common.internal"

// snake_case 分隔符.
internal const val SNAKE_CASE_SEPARATOR = "_"

// 所有field 后缀.
internal const val FIELD_POSTFIX = "_"
internal const val FIELD_POSTFIX_LEN = FIELD_POSTFIX.length

// Oneof field case后缀.
internal const val ONEOF_CASE_POSTFIX = "Case_"
internal const val ONEOF_CASE_POSTFIX_LEN = ONEOF_CASE_POSTFIX.length

// Builder 类名后缀.
internal const val BUILDER_POSTFIX = "\$Builder"
internal const val BUILDER_POSTFIX_LEN = BUILDER_POSTFIX.length

// 创建Builder方法名.
internal const val NEW_BUILDER_METHOD = "newBuilder"

// 枚举和int转换.
internal const val FOR_NUMBER = "forNumber"
internal const val GET_NUMBER = "getNumber"

//// 枚举和字符串转换
//internal const val VALUE_OF = "valueOf"

// Field name for any typeUrl.
internal const val ANY_TYPE_URL = "@type"
internal const val ANY_VALUE = "value"

// get field 方法前缀.
internal const val GET = "get"

// set single field 方法前缀
internal const val SET = "set"

// add repeated field 方法前缀.
internal const val ADD = "add"

// repeated field 中获得 list 方法后缀.
internal const val LIST = "List"

// map field put 方法前缀.
internal const val PUT = "put"

// map field 中获得 map 方法后缀.
internal const val MAP = "Map"

// map field 通过key获得value 方法后缀.
internal const val OR_THROW = "OrThrow"

//// 无法识别的 enum raw value.
//internal const val UNRECOGNIZED_ENUM_VALUE = -1
//
//internal const val UNRECOGNIZED_ENUM_VALUE_NAME = "UNRECOGNIZED"

// Set enum raw value 方法后缀.
internal const val VALUE = "Value"