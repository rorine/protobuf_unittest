package com.example.lib.moss.util.common.naming

import com.example.lib.moss.util.common.internal.BUILDER_POSTFIX
import com.example.lib.moss.util.common.internal.BUILDER_POSTFIX_LEN

/**
 * 描述：Proto message class name 向其他类型名字转换工具.
 *
 * Created by zhangyuling on 2023/6/28
 */

object ProtoMessageClassName {

    fun toBuilderName(className: String): String {
        return className + BUILDER_POSTFIX
    }

    fun fromBuilderName(builderName: String): String {
        return if (builderName.length > BUILDER_POSTFIX_LEN) builderName.dropLast(BUILDER_POSTFIX_LEN) else builderName
    }

}