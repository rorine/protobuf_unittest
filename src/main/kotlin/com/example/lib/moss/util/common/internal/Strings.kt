package com.example.lib.moss.util.common.internal

/**
 * 描述：处理格式变换.
 *
 * Created by zhangyuling on 2023/6/13
 */

val UNDERSCORE: String = "_"

/**
 * 用separator分割输入的cameral case.
 *
 * someFieldName ---> some_Field_Name
 * _someFieldName ---> _some_Field_Name
 * aStringField ---> a_String_Field
 * aURL ---> a_U_R_L
 */
internal fun separateCamelCase(name: String, separator: String = UNDERSCORE): String {
    val translation = StringBuilder()
    var i = 0
    val length = name.length
    while (i < length) {
        val character = name[i]
        if (Character.isUpperCase(character) && translation.length != 0) {
            translation.append(separator)
        }
        translation.append(character)
        i++
    }
    return translation.toString()
}

/**
 * lower_snake_case 或 UPPER_SNAKE_CASE 转 UpperCamelCase
 */
internal fun snakeToUpperCamelCase(raw: String, separator: String = UNDERSCORE): String {
    return raw.toLowerCase().split(separator).joinToString(separator = "") { it.capitalize() }
}

/**
 * lower_snake_case 或 UPPER_SNAKE_CASE 转 lowerCamelCase
 */
internal fun snakeToLowerCamelCase(raw: String, separator: String = UNDERSCORE): String {
    return raw.toLowerCase().split(separator).mapIndexed { index, s -> if (index == 0) s else s.capitalize() }
        .joinToString(separator = "")
}

/**
 * 首字母大写.
 *
 * someFieldName ---> SomeFieldName
 * _someFieldName ---> _SomeFieldName
 */
internal fun upperCaseFirstLetter(name: String): String {
    var firstLetterIndex = 0
    val limit = name.length - 1
    while (!Character.isLetter(name[firstLetterIndex]) && firstLetterIndex < limit) {
        ++firstLetterIndex
    }
    val firstLetter = name[firstLetterIndex]
    return if (Character.isUpperCase(firstLetter)) {
        name
    } else {
        val uppercased = Character.toUpperCase(firstLetter)
        if (firstLetterIndex == 0) uppercased.toString() + name.substring(1) else name.substring(0, firstLetterIndex) + uppercased + name.substring(firstLetterIndex + 1)
    }
}